import java.net.*;
import java.io.*;

public class Cliente {
	public static final String HOST_PADRAO = "localhost";
	public static final int PORTA_PADRAO = 3000;

	public static void main(String[] args) {
		if (args.length > 2) {
			System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
			return;
		}

		Socket conexao = null;
		try {
			String host = Cliente.HOST_PADRAO;
			int porta = Cliente.PORTA_PADRAO;

			if (args.length > 0)
				host = args[0];

			if (args.length == 2)
				porta = Integer.parseInt(args[1]);

			conexao = new Socket(host, porta);
		} catch (Exception erro) {
			System.err.println("Indique o servidor e a porta corretos!\n");
			return;
		}

		ObjectOutputStream transmissor = null;
		try {
			transmissor = new ObjectOutputStream(
					conexao.getOutputStream());
		} catch (Exception erro) {
			System.err.println("Indique o servidor e a porta corretos!\n");
			return;
		}

		ObjectInputStream receptor = null;
		try {
			receptor = new ObjectInputStream(
					conexao.getInputStream());
		} catch (Exception erro) {
			System.err.println("Indique o servidor e a porta corretos!\n");
			return;
		}

		Parceiro servidor = null;
		try {
			servidor = new Parceiro(conexao, receptor, transmissor);
		} catch (Exception erro) {
			System.err.println("Indique o servidor e a porta corretos!\n");
			return;
		}

		TratadoraDeComunicadoDeDesligamento tratadoraDeComunicadoDeDesligamento = null;
		try {
			tratadoraDeComunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
		} catch (Exception erro) {
		} // sei que servidor foi instanciado

		tratadoraDeComunicadoDeDesligamento.start();

		String opcao = "";
		do {
			System.out.print("Sua opcao (par, impar ou terminar)?");

			try {
				opcao = Teclado.getUmString().toLowerCase();
			} catch (Exception erro) {
				System.err.println("Opcao invalida!\n");
				continue;
			}

			if (!opcao.equals("par") && !opcao.equals("impar") && !opcao.equals("terminar")) {
				System.err.println("Opcao invalida!\n");
				continue;
			}

			try {
				int valor = 0;
				if (opcao.equals("par") || opcao.equals("impar")) {
					System.out.print("Valor? ");
					try {
						valor = Teclado.getUmInt();
						System.out.println();

						if (valor < 0) {
							System.err.println("Valor invalido!\n");
							continue;
						}
					} catch (Exception erro) {
						System.err.println("Valor invalido!\n");
						continue;
					}

					servidor.receba(new PedidoDeResolucao(opcao, valor));
					Comunicado comunicado = null;
					do {
						comunicado = (Comunicado) servidor.espie();
					} while (!(comunicado instanceof Resultado));
					Resultado resultado = (Resultado) servidor.envie();
					System.out.println(resultado.getResultado() + "\n");

				}
			} catch (Exception erro) {
				System.err.println("Erro de comunicacao com o servidor;");
				System.err.println("Tente novamente!");
				System.err.println("Caso o erro persista, termine o programa");
				System.err.println("e volte a tentar mais tarde!\n");
			}
		} while (!opcao.equals("terminar"));
		try {
			servidor.receba(new PedidoParaSair());
		} catch (Exception erro) {
		}

		System.out.println("Obrigado por usar este programa!");
		System.exit(0);
	}
}
