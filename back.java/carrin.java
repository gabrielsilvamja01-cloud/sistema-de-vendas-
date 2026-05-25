import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class carrin {
    
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
    
    // Classe que representa um Item no Carrinho
    public static class ItemCarrinho {
        private Produto produto;
        private int quantidade;
        private LocalDateTime dataAdicionado;
        
        public ItemCarrinho(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.dataAdicionado = LocalDateTime.now();
        }
        
        public Produto getProduto() { return produto; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
        public LocalDateTime getDataAdicionado() { return dataAdicionado; }
        
        @Override
        public String toString() {
            return "• " + produto.getNome() + " - Quantidade: " + quantidade;
        }
    }
    
    // Classe que gerencia o Carrinho com integração WhatsApp
    public static class Carrinho {
        private List<ItemCarrinho> itens;
        private String enderecoEntrega;
        private String nomeCliente;
        private static final double TAXA_FIXA_LIMEIRA = 10.00;
        private static final String REGIAO_ENTREGA = "Limeira, São Paulo";
        private static final String NUMERO_WHATSAPP = "+55 (19) 98169-9376";
        private static final String NUMERO_WHATSAPP_FORMATADO = "5519981699376";
        private static final DateTimeFormatter FORMATO_DATA = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        public Carrinho() {
            this.itens = new ArrayList<>();
            this.enderecoEntrega = "";
            this.nomeCliente = "";
        }
        
        // Métodos de gerenciamento do carrinho
        public void adicionarProduto(Produto produto, int quantidade) {
            if (quantidade <= 0) {
                System.out.println("✗ Quantidade deve ser maior que zero!");
                return;
            }
            
            for (ItemCarrinho item : itens) {
                if (item.getProduto().getId() == produto.getId()) {
                    item.setQuantidade(item.getQuantidade() + quantidade);
                    System.out.println("✓ Quantidade de '" + produto.getNome() + 
                                     "' atualizada para: " + item.getQuantidade());
                    return;
                }
            }
            
            itens.add(new ItemCarrinho(produto, quantidade));
            System.out.println("✓ '" + produto.getNome() + 
                             "' adicionado ao carrinho (Qtd: " + quantidade + ")");
        }
        
        public void removerProduto(int idProduto) {
            boolean removido = itens.removeIf(item -> item.getProduto().getId() == idProduto);
            if (removido) {
                System.out.println("✓ Produto removido do carrinho");
            } else {
                System.out.println("✗ Produto não encontrado no carrinho");
            }
        }
        
        public void atualizarQuantidade(int idProduto, int novaQuantidade) {
            if (novaQuantidade <= 0) {
                removerProduto(idProduto);
                return;
            }
            
            for (ItemCarrinho item : itens) {
                if (item.getProduto().getId() == idProduto) {
                    item.setQuantidade(novaQuantidade);
                    System.out.println("✓ Quantidade atualizada para: " + novaQuantidade);
                    return;
                }
            }
            System.out.println("✗ Produto não encontrado");
        }
        
        public void limparCarrinho() {
            itens.clear();
            System.out.println("✓ Carrinho limpo com sucesso");
        }
        
        public void setEnderecoEntrega(String endereco) {
            this.enderecoEntrega = endereco;
            System.out.println("✓ Endereço de entrega definido: " + endereco);
        }
        
        public void setNomeCliente(String nome) {
            this.nomeCliente = nome;
            System.out.println("✓ Nome do cliente definido: " + nome);
        }
        
        public String getEnderecoEntrega() { return enderecoEntrega; }
        public String getNomeCliente() { return nomeCliente; }
        public List<ItemCarrinho> obterItens() { return new ArrayList<>(itens); }
        
        public int obterTotalProdutos() {
            return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
        }
        
        public boolean estaVazio() {
            return itens.isEmpty();
        }
        
        // Métodos de visualização
        public void exibirCarrinho() {
            System.out.println("\n╔═══════════════════════════════════════════════╗");
            System.out.println("║          CARRINHO DE COMPRAS - LINHUZ         ║");
            System.out.println("╚═══════════════════════════════════════════════╝\n");
            
            if (itens.isEmpty()) {
                System.out.println("Seu carrinho está vazio!");
                return;
            }
            
            System.out.println("📦 PRODUTOS NO CARRINHO:");
            for (ItemCarrinho item : itens) {
                System.out.println("   " + item.toString());
            }
            
            System.out.println("\n───────────────────────────────────────────────");
            System.out.println("Total de itens: " + obterTotalProdutos());
            System.out.println("Taxa de entrega (Limeira, SP): R$ " + 
                             String.format("%.2f", TAXA_FIXA_LIMEIRA));
            System.out.println("Status: SOB ENCOMENDA");
            
            if (!enderecoEntrega.isEmpty()) {
                System.out.println("Endereço: " + enderecoEntrega);
            }
            if (!nomeCliente.isEmpty()) {
                System.out.println("Cliente: " + nomeCliente);
            }
            System.out.println("───────────────────────────────────────────────\n");
        }
        
        // Métodos WhatsApp
        private String gerarMensagemPedido() {
            if (itens.isEmpty()) {
                return "";
            }
            
            StringBuilder mensagem = new StringBuilder();
            mensagem.append("*PEDIDO LINHUZ*\n\n");
            mensagem.append("Olá! Gostaria de encomendar:\n\n");
            
            for (ItemCarrinho item : itens) {
                mensagem.append("• ").append(item.getProduto().getNome())
                        .append(" - Quantidade: ").append(item.getQuantidade()).append("\n");
            }
            
            mensagem.append("\n*Endereço de entrega:* ").append(enderecoEntrega).append("\n");
            mensagem.append("*Data do pedido:* ").append(LocalDateTime.now().format(FORMATO_DATA)).append("\n");
            mensagem.append("*Taxa de entrega:* R$ ").append(String.format("%.2f", TAXA_FIXA_LIMEIRA)).append("\n");
            mensagem.append("*Região:* ").append(REGIAO_ENTREGA).append("\n\n");
            mensagem.append("poderia e dar mais informações!");
            
            return mensagem.toString();
        }
        
        public String gerarLinkWhatsApp() {
            if (itens.isEmpty()) {
                System.out.println("✗ Carrinho vazio! Adicione produtos antes de finalizar.");
                return "";
            }
            
            if (enderecoEntrega.isEmpty()) {
                System.out.println("✗ Defina o endereço de entrega antes de finalizar!");
                return "";
            }
            
            String mensagem = gerarMensagemPedido();
            try {
                String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
                return "https://wa.me/" + NUMERO_WHATSAPP_FORMATADO + 
                       "?text=" + mensagemCodificada;
            } catch (Exception e) {
                System.out.println("✗ Erro ao gerar link WhatsApp: " + e.getMessage());
                return "";
            }
        }
        
        public void abrirWhatsApp() {
            String linkWhatsApp = gerarLinkWhatsApp();
            
            if (linkWhatsApp.isEmpty()) {
                return;
            }
            
            System.out.println("\n╔═══════════════════════════════════════════════╗");
            System.out.println("║      FINALIZANDO PEDIDO - WHATSAPP            ║");
            System.out.println("╚═══════════════════════════════════════════════╝\n");
            
            System.out.println("📱 Link gerado com sucesso!");
            System.out.println("🔗 Clique no link abaixo ou copie para seu navegador:\n");
            System.out.println(linkWhatsApp + "\n");
            
            System.out.println("📞 Número para contato: " + NUMERO_WHATSAPP);
            System.out.println("\n✓ Você será redirecionado para o WhatsApp com sua encomenda!\n");
            
            // Tentar abrir no navegador automaticamente (funciona em diferentes sistemas)
            try {
                abrirNavegador(linkWhatsApp);
            } catch (Exception e) {
                System.out.println("💡 Copie o link acima e cole no seu navegador!\n");
            }
        }
        
        private void abrirNavegador(String url) throws IOException {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // Windows
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
            } else if (os.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else if (os.contains("nux")) {
                // Linux
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
            
            System.out.println("🌐 Abrindo navegador...\n");
        }
        
        public void exibirResumo() {
            System.out.println("\n╔═══════════════════════════════════════════════╗");
            System.out.println("║        RESUMO DO PEDIDO - LINHUZ              ║");
            System.out.println("╚═══════════════════════════════════════════════╝\n");
            
            System.out.println("👤 Cliente: " + (nomeCliente.isEmpty() ? "Não informado" : nomeCliente));
            System.out.println("📍 Endereço: " + (enderecoEntrega.isEmpty() ? "Não informado" : enderecoEntrega));
            System.out.println("📅 Data: " + LocalDateTime.now().format(FORMATO_DATA));
            System.out.println("\n📦 ITENS:");
            
            for (ItemCarrinho item : itens) {
                System.out.println("   " + item.toString());
            }
            
            System.out.println("\n───────────────────────────────────────────────");
            System.out.println("Total de itens: " + obterTotalProdutos());
            System.out.println("Taxa de entrega: R$ " + String.format("%.2f", TAXA_FIXA_LIMEIRA));
            System.out.println("Região: " + REGIAO_ENTREGA);
            System.out.println("Status: ✓ SOB ENCOMENDA");
            System.out.println("───────────────────────────────────────────────\n");
        }
    }
    
    // Método de teste
    public static void main(String[] args) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   SISTEMA DE CARRINHO - LOJA LINHUZ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Criar produtos
        Produto filtro = new Produto(1, "Filtro dos Sonhos", "Lindo filtro artesanal");
        Produto bolca = new Produto(2, "Bolça de Crochê", "Bolsa elegante");
        Produto bincos = new Produto(3, "Bincos Artesanais", "Bancos decorativos");
        Produto top = new Produto(4, "Top de Crochê", "Top moderno");
        Produto bandana = new Produto(5, "Bandana", "Bandana estilosa");
        
        // Criar carrinho
        Carrinho carrinho = new Carrinho();
        
        // Definir dados do cliente
        carrinho.setNomeCliente("João Silva");
        carrinho.setEnderecoEntrega("Rua das Flores, 123 - Limeira, SP");
        
        // Adicionar produtos
        System.out.println(">>> Adicionando produtos ao carrinho...\n");
        carrinho.adicionarProduto(filtro, 1);
        carrinho.adicionarProduto(bolca, 2);
        carrinho.adicionarProduto(bincos, 1);
        carrinho.adicionarProduto(top, 1);
        carrinho.adicionarProduto(bandana, 3);
        
        // Exibir carrinho
        carrinho.exibirCarrinho();
        
        // Exemplos de operações
        System.out.println(">>> Demonstração de operações do carrinho:\n");
        carrinho.atualizarQuantidade(2, 5); // Atualizar quantidade de Bolça
        carrinho.adicionarProduto(bandana, 2); // Adicionar mais bandanas
        
        // Exibir carrinho atualizado
        carrinho.exibirCarrinho();
        
        // Exibir resumo
        carrinho.exibirResumo();
        
        // Gerar link WhatsApp e abrir
        System.out.println(">>> Finalizando pedido...\n");
        carrinho.abrirWhatsApp();
    }
}
