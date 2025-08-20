package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Contract;
import za.ac.cput.domain.Job;
import za.ac.cput.domain.User;
import za.ac.cput.repository.ContractRepository;
import za.ac.cput.factory.ContractFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ContractService implements IService<Contract, String> {

    private final ContractRepository contractRepository;
    private final JobService jobService;
    private final NotificationService notificationService;

    @Autowired
    public ContractService(ContractRepository contractRepository,
                           JobService jobService,
                           NotificationService notificationService) {
        this.contractRepository = contractRepository;
        this.jobService = jobService;
        this.notificationService = notificationService;
    }

    @Override
    public Contract create(Contract contract) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateContract(contract);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid contract data: " + result.getErrorMessage());
        }

        Contract savedContract = contractRepository.save(contract);

        // Update job status to ASSIGNED
        jobService.assignJob(contract.getJob().getJobId(), contract.getWorker());

        // Notify both parties
        notificationService.createSystemNotification(
                contract.getClient(),
                "Contract created for job: " + contract.getJob().getTitle()
        );
        notificationService.createSystemNotification(
                contract.getWorker(),
                "You have a new contract for: " + contract.getJob().getTitle()
        );

        return savedContract;
    }

    @Override
    public Contract read(String contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + contractId));
    }

    @Override
    public Contract update(Contract contract) {
        if (!contractRepository.existsById(contract.getContractId())) {
            throw new RuntimeException("Contract not found");
        }
        return contractRepository.save(contract);
    }

    @Override
    public Contract delete(String contractId) {
        Contract contract = read(contractId);
        contractRepository.deleteById(contractId);
        return contract;
    }

    // Business Logic Methods
    public Contract createContract(Job job, User client, User worker, LocalDateTime startDate,
                                   LocalDateTime endDate, Double agreedPay, String terms) {
        Contract contract = ContractFactory.createContract(job, client, worker, startDate, endDate, agreedPay, terms);
        if (contract == null) {
            throw new IllegalArgumentException("Invalid contract creation data");
        }
        return create(contract);
    }

    public Contract completeContract(String contractId) {
        Contract contract = read(contractId);
        if (contract.getStatus() != Contract.ContractStatus.ACTIVE) {
            throw new IllegalStateException("Contract is not active");
        }

        Contract completedContract = update(new Contract.Builder()
                .copy(contract)
                .setStatus(Contract.ContractStatus.COMPLETED)
                .build());

        // Update job status
        jobService.completeJob(contract.getJob().getJobId());

        // Notify both parties
        notificationService.createSystemNotification(
                contract.getClient(),
                "Contract completed for: " + contract.getJob().getTitle()
        );
        notificationService.createSystemNotification(
                contract.getWorker(),
                "Contract completed for: " + contract.getJob().getTitle()
        );

        return completedContract;
    }

    public Contract cancelContract(String contractId, String reason) {
        Contract contract = read(contractId);
        if (contract.getStatus() == Contract.ContractStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed contract");
        }

        return update(new Contract.Builder()
                .copy(contract)
                .setStatus(Contract.ContractStatus.CANCELLED)
                .build());
    }

    public Contract markAsDisputed(String contractId) {
        Contract contract = read(contractId);
        return update(new Contract.Builder()
                .copy(contract)
                .setStatus(Contract.ContractStatus.DISPUTED)
                .build());
    }

    public List<Contract> findActiveContracts() {
        return contractRepository.findActiveContracts();
    }

    public List<Contract> findContractsByClient(User client) {
        return contractRepository.findByClient(client);
    }

    public List<Contract> findContractsByWorker(User worker) {
        return contractRepository.findByWorker(worker);
    }

    public List<Contract> findOverdueContracts() {
        return contractRepository.findOverdueContracts(LocalDateTime.now());
    }

    public List<Contract> findDisputedContracts() {
        return contractRepository.findDisputedContracts();
    }

    public boolean hasActiveContracts(User user) {
        return contractRepository.hasActiveContracts(user);
    }

    public List<Contract> findContractsByUser(User user) {
        return contractRepository.findByClientOrWorker(user);
    }
}