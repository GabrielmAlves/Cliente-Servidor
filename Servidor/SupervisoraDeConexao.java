import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread {
    private double valor = 0;
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios)
            throws Exception {
        if (conexao == null)
            throw new Exception("Conexao ausente");

        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    public void run() {

        ObjectOutputStream transmissor;
        try {
            transmissor = new ObjectOutputStream(
                    this.conexao.getOutputStream());
        } catch (Exception erro) {
            return;
        }

        ObjectInputStream receptor = null;
        try {
            receptor = new ObjectInputStream(
                    this.conexao.getInputStream());
        } catch (Exception err0) {
            try {
                transmissor.close();
            } catch (Exception falha) {
            } // so tentando fechar antes de acabar a thread

            return;
        }

        try {
            this.usuario = new Parceiro(this.conexao,
                    receptor,
                    transmissor);
        } catch (Exception erro) {
        } // sei que passei os parametros corretos

        try {
            synchronized (this.usuarios) {
                this.usuarios.add(this.usuario);
            }

            for (;;) {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null)
                    return;
                else if (comunicado instanceof PedidoDeResolucao) {
                    int numeroRandom = new Random().nextInt();
                    PedidoDeResolucao pedidoDeResolucao = (PedidoDeResolucao) comunicado;
                    int soma = numeroRandom + pedidoDeResolucao.getValor();

                    switch (pedidoDeResolucao.getOperacao()) {
                        case "par":
                            if (soma % 2 == 0) {
                                usuario.receba(new Resultado("O número final é: "+soma+", você ganhou!"));
                            } else {
                                usuario.receba(new Resultado("O número final é: "+soma+", você perdeu!"));
                            }
                            break;
                        case "impar":
                            if (soma % 2 == 0) {
                                usuario.receba(new Resultado("O número final é: "+soma+", você perdeu"));
                            } else {
                                usuario.receba(new Resultado("O número final é: "+soma+", você ganhou"));
                            }
                            break;
                    }
                }

                else if (comunicado instanceof PedidoParaSair) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                }
            }
        } catch (Exception erro) {
            try {
                transmissor.close();
                receptor.close();
            } catch (Exception falha) {
            } // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
