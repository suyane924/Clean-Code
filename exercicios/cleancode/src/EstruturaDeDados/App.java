package EstruturaDeDados;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

class Livro {
    String titulo;
    String autor;
    Usuario emprestadoPara;

    Livro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
        this.emprestadoPara = null; 
    }

    boolean estaDisponivel() {
        return emprestadoPara == null;
    }
}

class Usuario {
    String nome;

    Usuario(String nome) {
        this.nome = nome;
    }
}

class Biblioteca {
    List<Livro> livros = new ArrayList<>();
    List<Usuario> usuarios = new ArrayList<>();
    String logFile = "erros.log";

    void cadastrarLivro(String titulo, String autor) {
        try {
            if (titulo == null || titulo.trim().equals("")) {
                throw new Exception("título inválido");
            }
            livros.add(new Livro(titulo, autor));
            System.out.println("✅ Livro \"" + titulo + "\" cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao cadastrar livro: " + titulo + " - " + e.getMessage());
        }
    }

    void cadastrarUsuario(String nome) {
        try {
            if (nome == null || nome.trim().equals("")) {
                throw new Exception("nome inválido");
            }
            usuarios.add(new Usuario(nome));
            System.out.println("✅ Usuário \"" + nome + "\" cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao cadastrar usuário: " + nome + " - " + e.getMessage());
        }
    }

    void emprestarLivro(String titulo, String nomeUsuario) {
        try {
            Livro livro = buscarLivro(titulo);
            if (livro == null) throw new Exception("Livro não existe");

            Usuario usuario = buscarUsuario(nomeUsuario);
            if (usuario == null) throw new Exception("Usuário não existe");

            if (!livro.estaDisponivel()) throw new Exception("Livro já emprestado");

            livro.emprestadoPara = usuario;
            System.out.println("✅ Livro \"" + titulo + "\" emprestado para \"" + nomeUsuario + "\"");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao emprestar livro: " + titulo + " para " + nomeUsuario + " - " + e.getMessage());
        }
    }

    void devolverLivro(String titulo, String nomeUsuario) {
        try {
            Livro livro = buscarLivro(titulo);
            if (livro == null) throw new Exception("Livro não existe");

            if (livro.emprestadoPara == null || !livro.emprestadoPara.nome.equals(nomeUsuario)) {
                throw new Exception("Livro não estava emprestado");
            }

            livro.emprestadoPara = null;
            System.out.println("✅ Livro \"" + titulo + "\" devolvido por \"" + nomeUsuario + "\"");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao devolver livro: " + titulo + " por " + nomeUsuario + " - " + e.getMessage());
        }
    }

    void exibirRelatorio() {
        System.out.println("\n📚 Relatório da Biblioteca:");
        for (Livro livro : livros) {
            if (livro.estaDisponivel()) {
                System.out.println("- " + livro.titulo + " → disponível");
            } else {
                System.out.println("- " + livro.titulo + " → emprestado para " + livro.emprestadoPara.nome);
            }
        }
    }

    Livro buscarLivro(String titulo) {
        for (Livro livro : livros) {
            if (livro.titulo.equals(titulo)) return livro;
        }
        return null;
    }

    Usuario buscarUsuario(String nome) {
        for (Usuario u : usuarios) {
            if (u.nome.equals(nome)) return u;
        }
        return null;
    }

    void registrarErro(String mensagem) {
        try (FileWriter fw = new FileWriter(logFile, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(mensagem);
        } catch (IOException e) {
            System.out.println("Não foi possível registrar o erro no log: " + e.getMessage());
        }
    }
}

public class App {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        biblioteca.cadastrarLivro("O Senhor dos Anéis", "J. R. R. Tolkien");
        biblioteca.cadastrarLivro("", null); // ❌
        biblioteca.cadastrarLivro("Dom Quixote", "Miguel de Cervantes");
        biblioteca.cadastrarLivro("Clean Code", "Robert C. Martin");

        biblioteca.cadastrarUsuario("Alice");
        biblioteca.cadastrarUsuario(""); // ❌
        biblioteca.cadastrarUsuario("Bruno");
        biblioteca.cadastrarUsuario("Carla");

        biblioteca.emprestarLivro("O Senhor dos Anéis", "Alice");
        biblioteca.emprestarLivro("O Senhor dos Anéis", "Bruno"); // ❌ 
        biblioteca.emprestarLivro("Clean Code", "Carla");
        biblioteca.emprestarLivro("Livro Fantasma", "Alice"); // ❌ 

        biblioteca.devolverLivro("Dom Quixote", "Bruno"); // ❌ 
        biblioteca.devolverLivro("Clean Code", "Carla");

        biblioteca.exibirRelatorio();
    }
}
