package com.SecureBankingApi.infrastructure.persistence.transaction;

import com.SecureBankingApi.domain.PageRequest;
import com.SecureBankingApi.domain.PageResult;
import com.SecureBankingApi.domain.transaction.Transaction;
import com.SecureBankingApi.domain.transaction.TransactionRepository;
import com.SecureBankingApi.domain.transaction.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {
    private SpringDataTransactionRepository repository;
    private TransactionMapper mapper;

    public TransactionRepositoryAdapter(SpringDataTransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(Transaction transaction) {
        TransactionJpaEntity entity = mapper.toEntity(transaction);
        repository.save(entity);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        Optional<TransactionJpaEntity> entity = repository.findById(id);
        return entity.map((e) -> mapper.toDomain(e));
    }

    @Override
    public List<Transaction> findBySourceUserId(UUID userId) {
        List<TransactionJpaEntity> entities = repository.findBySourceUserId(userId);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        List<TransactionJpaEntity> entities = repository.findByAccountId(accountId);
        return mapper.toDomainList(entities);
    }

    @Override
    public PageResult<Transaction> findByAccountIdPage(UUID accountId, PageRequest request) {
        Sort sort = resolveSort(request);

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );

        Page<TransactionJpaEntity> page = repository.findByAccountId(accountId, pageable);

        List<Transaction> content = page.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(request.getPage(), request.getSize(), page.getTotalElements(), content);
    }




    private Sort resolveSort(PageRequest request) {
        Sort.Direction dir = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return switch (request.getSortBy()){
            case "amount" -> Sort.by(dir, "amount");
            case "status" -> Sort.by(dir, "status");
            case "type" -> Sort.by(dir, "type");
            default -> Sort.by(dir, "createdAt");
        };
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        List<TransactionJpaEntity> entities = repository.findByStatus(status);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Transaction> findByPeriod(UUID accountId, LocalDateTime start, LocalDateTime end) {
        List<TransactionJpaEntity> entities = repository.findByPeriod(accountId, start, end);
        return mapper.toDomainList(entities);
    }
}
