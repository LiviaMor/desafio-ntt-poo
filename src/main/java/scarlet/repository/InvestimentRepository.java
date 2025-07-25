package scarlet.repository;


import scarlet.exception.AccountUseException;
import scarlet.exception.InvestimentNotFoundException;
import scarlet.exception.WalletNotFoundException;
import scarlet.model.AccountWallet;
import scarlet.model.Investiment;
import scarlet.model.InvestimentWallet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class InvestimentRepository {

    private long nextId = 0;
    private final List<Investiment> investments = new ArrayList<>();
    private final List<InvestimentWallet> wallets = new ArrayList<>();

    public Investiment create(final long tax, final long initialFunds){
        this.nextId++;
        var investment = new Investiment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestimentWallet initInvestment(final AccountWallet account, final long id){
        if (!wallets.isEmpty()) {
            var accountInUse = wallets.stream().map(InvestimentWallet::getAccount).toList();
            if (accountInUse.contains(account)) {
                throw new AccountUseException("A conta '" + account + "' já possui um investimento");
            }
        }

        var investiment = findById(id);
        checkFundsForTransaction(account, investiment.initialFunds());
        var wallet = new InvestimentWallet(investiment, account, investiment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    private void checkFundsForTransaction(AccountWallet account, long amount) {
        if (account.getFunds() < amount) {
            throw new RuntimeException("Fundos insuficientes na conta para iniciar o investimento");
        }
    }

    private void checkFundsForTransaction(InvestimentWallet wallet, long amount) {
        if (wallet.getFunds() < amount) {
            throw new RuntimeException("Fundos insuficientes na carteira de investimento");
        }
    }

    public InvestimentWallet deposit(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Deposito de investimento");
        return wallet;
    }

    public InvestimentWallet withdraw(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Retirada de investimento");
        if (wallet.getFunds() == 0) wallets.remove(wallet);
        return wallet;
    }

    public void updateAmount(){
        wallets.forEach(w -> w.updateAmount(w.getInvestiment().tax()));
    }

    public Investiment findById(final long id){
        return investments.stream()
                .filter( a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                        ()-> new InvestimentNotFoundException("O investimento '"+id+"' não foi encontrado")
                );
    }

    public InvestimentWallet findWalletByAccountPix(final String pix){
        return wallets.stream()
                .filter( w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        ()-> new WalletNotFoundException("A carteira não foi encontrada")
                );
    }

    public List<InvestimentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investiment> list() {
        return Collections.unmodifiableList(investments);
    }

}