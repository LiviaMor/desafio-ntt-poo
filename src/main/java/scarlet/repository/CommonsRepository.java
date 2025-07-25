package scarlet.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import scarlet.exception.NotFundsEnoughException;
import scarlet.model.AccountWallet;
import scarlet.model.BankService;
import scarlet.model.Money;
import scarlet.model.MoneyAudit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonsRepository {
    public static void checkFundsForTranscation(final AccountWallet source, final long amount) {
        if(source.getFunds() < amount) {
            throw new NotFundsEnoughException(" Saldo insuficiente");
        }
    }
    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description) {
        var history = new MoneyAudit(transactionId, BankService.ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(()-> new Money(history)).limit(funds).toList();
    }
}
