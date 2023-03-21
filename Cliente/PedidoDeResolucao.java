public class PedidoDeResolucao extends Comunicado {

    private String operacao;
    private int valor;
  
    public PedidoDeResolucao(String operacao, int valor)
    {
      this.operacao = operacao;
      this.valor = valor;
    }
  
    public String getOperacao()
      {
          return this.operacao;
      }
  
    public int getValor()
      {
          return this.valor;
      }
  }