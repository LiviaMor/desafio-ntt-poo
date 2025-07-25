package scarlet.model;

import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;


@ToString
@Getter
public class InvestimentWallet extends Wallet {
    private final Investiment investiment;
    private final AccountWallet account;

    public InvestimentWallet(BankService serviceType, Investiment investiment, final AccountWallet account, final long amount) {
        super(BankService.INVESTIMENT);
        this.investiment = investiment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getServiceType(),  "investimento");

    }
    public void updateAmount(long percent) {
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getServiceType(), "rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);

    }

}
