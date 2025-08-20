package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Dispute;
import za.ac.cput.domain.User;
import za.ac.cput.repository.DisputeRepository;
import za.ac.cput.factory.DisputeFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DisputeService implements IService<Dispute, String> {

    private final DisputeRepository disputeRepository;
    private final ContractService contractService;
    private final NotificationService notificationService;

    @Autowired
    public DisputeService(DisputeRepository disputeRepository,
                          ContractService contractService,
                          NotificationService notificationService) {
        this.disputeRepository = disputeRepository;
        this.contractService = contractService;
        this.notificationService = notificationService;
    }

    @Override
    public Dispute create(Dispute dispute) {
        if (dispute == null) {
            throw new IllegalArgumentException("Dispute cannot be null");
        }

        Dispute savedDispute = disputeRepository.save(dispute);

        // Mark contract as disputed
        contractService.markAsDisputed(dispute.getContract().getContractId());

        // Notify both parties
        Contract contract = dispute.getContract();
        User otherParty = dispute.getOpenedBy().equals(contract.getClient()) ?
                contract.getWorker() : contract.getClient();

        notificationService.createSystemNotification(
                otherParty,
                "A dispute has been opened for contract: " + contract.getJob().getTitle()
        );

        // Notify admins
        notificationService.createSystemNotification(
                null, // This would need admin notification logic
                "New dispute opened for contract: " + contract.getContractId()
        );

        return savedDispute;
    }

    @Override
    public Dispute read(String disputeId) {
        return disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found with ID: " + disputeId));
    }

    @Override
    public Dispute update(Dispute dispute) {
        if (!disputeRepository.existsById(dispute.getDisputeId())) {
            throw new RuntimeException("Dispute not found");
        }
        return disputeRepository.save(dispute);
    }

    @Override
    public Dispute delete(String disputeId) {
        Dispute dispute = read(disputeId);
        disputeRepository.deleteById(disputeId);
        return dispute;
    }

    // Business Logic Methods
    public Dispute openDispute(Contract contract, User openedBy, String reason) {
        // Validate contract can have dispute
        if (contract.getStatus() == Contract.ContractStatus.COMPLETED) {
            throw new IllegalStateException("Cannot dispute completed contract");
        }

        // Validate user is part of the contract
        if (!contract.getClient().equals(openedBy) && !contract.getWorker().equals(openedBy)) {
            throw new IllegalArgumentException("User is not part of this contract");
        }

        Dispute dispute = DisputeFactory.createDispute(contract, openedBy, reason);
        if (dispute == null) {
            throw new IllegalArgumentException("Invalid dispute creation data");
        }

        return create(dispute);
    }

    public Dispute resolveDispute(String disputeId, String resolutionDetails, User resolvedBy) {
        Dispute dispute = read(disputeId);

        if (dispute.getStatus() != Dispute.DisputeStatus.OPEN) {
            throw new IllegalStateException("Dispute is not open");
        }

        Dispute resolvedDispute = update(new Dispute.Builder()
                .copy(dispute)
                .setStatus(Dispute.DisputeStatus.RESOLVED)
                .setResolutionDetails(resolutionDetails)
                .build());

        // Notify both parties
        Contract contract = dispute.getContract();
        notificationService.createSystemNotification(
                contract.getClient(),
                "Dispute resolved for contract: " + contract.getJob().getTitle()
        );
        notificationService.createSystemNotification(
                contract.getWorker(),
                "Dispute resolved for contract: " + contract.getJob().getTitle()
        );

        return resolvedDispute;
    }

    public Dispute dismissDispute(String disputeId, User dismissedBy) {
        Dispute dispute = read(disputeId);

        if (dispute.getStatus() != Dispute.DisputeStatus.OPEN) {
            throw new IllegalStateException("Dispute is not open");
        }

        return update(new Dispute.Builder()
                .copy(dispute)
                .setStatus(Dispute.DisputeStatus.DISMISSED)
                .build());
    }

    public List<Dispute> findDisputesByContract(Contract contract) {
        return disputeRepository.findByContract(contract);
    }

    public List<Dispute> findDisputesByUser(User user) {
        return disputeRepository.findByOpenedBy(user);
    }

    public List<Dispute> findOpenDisputes() {
        return disputeRepository.findOpenDisputes();
    }

    public List<Dispute> findDisputesByStatus(Dispute.DisputeStatus status) {
        return disputeRepository.findByStatus(status);
    }

    public List<Dispute> findDisputesOpenedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return disputeRepository.findByDateOpenedBetween(startDate, endDate);
    }

    public List<Dispute> findDisputesByContractParticipant(User user) {
        return disputeRepository.findByContractParticipant(user);
    }

    public Long countDisputesByStatus(Dispute.DisputeStatus status) {
        return disputeRepository.countByStatus(status);
    }

    public boolean hasOpenDisputes(Contract contract) {
        return !disputeRepository.findByContract(contract).stream()
                .filter(d -> d.getStatus() == Dispute.DisputeStatus.OPEN)
                .toList().isEmpty();
    }
}