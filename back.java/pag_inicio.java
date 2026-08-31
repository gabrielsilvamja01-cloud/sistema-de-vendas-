import java.util.ArrayList;
import java.util.List;

public class pag_inicio {
    
    // Classe que representa um Produto
    public static class Produto {
        private int id;
        private String nome;
        private String descricao;
        private boolean emEncomenda;
        
        public Produto(int id, String nome, String descricao) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.emEncomenda = true;
        }
        
        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public boolean isEmEncomenda() { return emEncomenda; }
        
        @Override
        public String toString() {
            return "ID: " + id + " | Produto: " + nome + 
                   " | Descrição: " + descricao + 
                   " | Status: Sob Encomenda";
        }
    }
    
    // Classe que gerencia a Loja
    public static class LojaLINHUZ {
        private List<Produto> catalogoProdutos;
        private carrin.Carrinho carrinho;
        
        public LojaLINHUZ() {
            this.carrinho = new carrin.Carrinho();
            this.catalogoProdutos = new ArrayList<>();
            inicializarCatalogo();
        }
        
        private void inicializarCatalogo() {
            catalogoProdutos.add(new Produto(1, "Filtro dos Sonhos", 
                "Lindo filtro dos sonhos artesanal"));
            catalogoProdutos.add(new Produto(2, "Bolça de Crochê", 
                "Bolsa elegante feita em crochê"));
            catalogoProdutos.add(new Produto(3, "Bincos Artesanais", 
                "Bancos decorativos artesanais"));
            catalogoProdutos.add(new Produto(4, "Top de Crochê", 
                "Top moderno em crochê fino"));
            catalogoProdutos.add(new Produto(5, "Bandana", 
                "Bandana estilosa e confortável"));
        }
        
        public void exibirCatalogo() {
            System.out.println("\n╔═══════════════════════════════════════════════╗");
            System.out.println("║      BEM-VINDO À LOJA LINHUZ!                 ║");
            System.out.println("║      Todos os produtos sob encomenda          ║");
            System.out.println("║      Taxa fixa: R$ 10,00 (Limeira, SP)        ║");
            System.out.println("╚═══════════════════════════════════════════════╝\n");
            
            System.out.println("📦 CATÁLOGO DE PRODUTOS:");
            System.out.println("───────────────────────────────────────────────");
            for (Produto p : catalogoProdutos) {
                System.out.println(p.toString());
            }
            System.out.println("───────────────────────────────────────────────\n");
        }
        
        public void adicionarProdutoAoCarrinho(int idProduto, int quantidade) {
            Produto produto = buscarProdutoPorId(idProduto);
            if (produto != null) {
                carrin.Produto produtoCarrinho = new carrin.Produto(
                    produto.getId(), produto.getNome(), produto.getDescricao()
                );
                carrinho.adicionarProduto(produtoCarrinho, quantidade);
            } else {
                System.out.println("✗ Produto com ID " + idProduto + " não encontrado!");
            }
        }
        
        private Produto buscarProdutoPorId(int id) {
            for (Produto p : catalogoProdutos) {
                if (p.getId() == id) {
                    return p;
                }
            }
            return null;
        }
        
        public carrin.Carrinho obterCarrinho() {
            return carrinho;
        }
        
        public List<Produto> obterCatalogo() {
            return new ArrayList<>(catalogoProdutos);
        }
    }
    
    // Método principal para teste
    public static void main(String[] args) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   PÁGINA INICIAL - LOJA LINHUZ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Criar instância da loja
        LojaLINHUZ loja = new LojaLINHUZ();
        
        // Exibir catálogo
        loja.exibirCatalogo();
        
        // Simular operações
        System.out.println(">>> Sistema de loja inicializado!");
        System.out.println(">>> Use a classe 'carrin' para gerenciar o carrinho!");
        System.out.println(">>> Use o método 'adicionarProdutoAoCarrinho()' para adicionar itens.\n");
    }
}
