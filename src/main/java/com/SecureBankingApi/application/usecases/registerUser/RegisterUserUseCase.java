package com.SecureBankingApi.application.usecases.registerUser;

import com.SecureBankingApi.application.exceptions.CpfAlreadyExistsException;
import com.SecureBankingApi.application.exceptions.EmailAlreadyExistsException;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountRequest;
import com.SecureBankingApi.application.usecases.createAccount.CreateAccountUseCase;
import com.SecureBankingApi.domain.account.AccountType;
import com.SecureBankingApi.domain.user.ports.PasswordHasher;
import com.SecureBankingApi.domain.user.User;
import com.SecureBankingApi.domain.user.ports.UserRepository;
import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.valueObjects.CPF;

public class RegisterUserUseCase {
    private final PasswordHasher hasher;
    private final UserRepository repository;
    private final CreateAccountUseCase createAccountUseCase;

    public RegisterUserUseCase(PasswordHasher hasher,
                               UserRepository repository,
                               CreateAccountUseCase createAccountUseCase) {
        this.hasher = hasher;
        this.repository = repository;
        this.createAccountUseCase = createAccountUseCase;
    }

    public RegisterUserResponse execute(RegisterUserRequest request){
        if(repository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        CPF cpf = new CPF(request.getCpf());
        if(repository.existsByCpf(cpf)){
            throw new CpfAlreadyExistsException(cpf.getValue());
        }
        String passwordHashed = hasher.hash(request.getPassword());

        User user = User.create(
                request.getEmail(),
                request.getFullName(),
                cpf,
                passwordHashed,
                UserRole.USER
                );


        try {
            User savedUser = repository.save(user);
            createAccountUseCase.execute(new CreateAccountRequest(savedUser.getId(), AccountType.CHECKING));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }

        return new RegisterUserResponse(
                user.getId(),
                user.getFullName(),
                user.getCpf().getValue(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
