package scarlet;

import scarlet.exception.AccountNotFoundException;
import scarlet.exception.NotFundsEnoughException;
import scarlet.model.AccountWallet;
import scarlet.repository.AccountRepository;
import scarlet.repository.InvestimentRepository;

import java.util.Arrays;
import java.util.Scanner;



public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestimentRepository investimentRepository = new InvestimentRepository();


    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá, seja bem vindo ao DIO bank");
        while(true){
            System.out.println("Selecione a opção desejada");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar da conta");
            System.out.println("6 - Transferência entre contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimenro");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");

            var option = scanner.nextInt();
            switch (option){
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createWalletInvestment();
                case 4 ->  deposit();
                case 5 ->  withdraw();
                case 6 ->  transferToAccount();
                case 7 ->  incInvestment();
                case 8 ->  rescueInvestment();
                case 9 ->  accountRepository.list().forEach(System.out::println);
                case 10 -> investimentRepository.list().forEach(System.out::println);
                case 11 -> investimentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investimentRepository.updateAmount();
                    System.out.println("Investimentos atualizados");
                }
                case 13 ->  checkHistory();
                case 14 ->  System.exit(0);
                default ->  System.out.println("Opção inválida!");

            }


        }
    }

    private static void createAccount(){
        System.out.println("Informe as chaves pix (separadas por '.')");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito");
        var amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada ");
    }

    private static void createInvestment(){
        System.out.println("Informe a taxa do investimento");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito");
        var initialFunds = scanner.nextLong();
        var investment = investimentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado: "+ investment);
    }

    private static void deposit(){
        System.out.println("Informe a chave pix da conta para depósito: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser depositado: ");
        var amount = scanner.nextLong();
        try {
            accountRepository.deposit(pix, amount);
        }catch(AccountNotFoundException ex){
            System.out.println(ex.getMessage());

        }
    }

    private static void withdraw(){
        System.out.println("Informe a chave pix da conta para saque: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser sacado: ");
        var amount = scanner.nextLong();
        try{
            accountRepository.withdraw(pix, amount);
        }catch(NotFundsEnoughException | AccountNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void transferToAccount(){
        System.out.println("Informe a chave pix da conta de origem: ");
        var source = scanner.next();
        System.out.println("Informe a chave pix da conta de destino: ");
        var target = scanner.next();
        System.out.println("Informe o valor a ser depositado: ");
        var amount = scanner.nextLong();
        try {
            accountRepository.transferMoney(source, target, amount);
        }catch(AccountNotFoundException ex){
            System.out.println(ex.getMessage());

        }
    }

    private static void createWalletInvestment(){
        System.out.println("Informe a chave pix da conta: ");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento: ");
        var investimentId = scanner.nextInt();
        var investmentWallet = investimentRepository.initInvestment(account, investimentId );
        System.out.println("Conta de investimento criada: "+investmentWallet);
    }

    private static void incInvestment(){
        System.out.println("Informe a chave pix da conta para investimento: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser investido: ");
        var amount = scanner.nextLong();
        try {
            investimentRepository.deposit(pix, amount);
        }catch(AccountNotFoundException ex){
            System.out.println(ex.getMessage());

        }
    }

    private static void rescueInvestment(){
        System.out.println("Informe a chave pix da conta para resgate do investimento: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser sacado: ");
        var amount = scanner.nextLong();
        try{
            investimentRepository.withdraw(pix, amount);
        }catch(NotFundsEnoughException | AccountNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }


    private static void checkHistory(){
        System.out.println("Informe a chave pix da conta para verificar extrato: ");
        var pix = scanner.next();
        AccountWallet wallet;
        try{
            wallet = accountRepository.findByPix(pix);
            var audit = wallet.getFinancialTransactions();
            System.out.println(audit);

        }catch(AccountNotFoundException ex){
            System.out.println(ex.getMessage());

        }

    }


}