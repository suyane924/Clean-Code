package TratamentoDeErros;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

class Partida {
    String timeA;
    String timeB;
    int golsA;
    int golsB;

    Partida(String timeA, String timeB, int golsA, int golsB) {
        this.timeA = timeA;
        this.timeB = timeB;
        this.golsA = golsA;
        this.golsB = golsB;
    }
}

class Torneio {
    List<String> times = new ArrayList<>();
    List<Partida> partidas = new ArrayList<>();
    String logFile = "erros.log";

    void adicionarTime(String nome) {
        try {
            if (nome == null || nome.trim().equals("")) {
                throw new Exception("Nome inválido");
            }
            times.add(nome);
            System.out.println("✅ Time \"" + nome + "\" adicionado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao adicionar time: " + nome + " - " + e.getMessage());
        }
    }

    void criarPartida(String timeA, String timeB, int golsA, int golsB) {
        try {
            if (!times.contains(timeA) || !times.contains(timeB)) {
                throw new Exception("Time não existe");
            }
            if (golsA < 0 || golsB < 0) {
                throw new Exception("Número inválido de gols");
            }
            partidas.add(new Partida(timeA, timeB, golsA, golsB));
            System.out.println("✅ Partida entre \"" + timeA + "\" e \"" + timeB + "\" criada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
            registrarErro("Erro ao criar partida: " + timeA + " x " + timeB + " - " + e.getMessage());
        }
    }

    void registrarErro(String mensagem) {
        try (FileWriter fw = new FileWriter(logFile, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(mensagem);
        } catch (IOException e) {
            System.out.println("Não foi possível registrar o erro no log: " + e.getMessage());
        }
    }

    ResultadoTorneio jogar() {
        Map<String, Integer> pontos = new HashMap<>();
        for (String time : times) {
            pontos.put(time, 0);
        }

        for (Partida p : partidas) {
            if (p.golsA > p.golsB) {
                pontos.put(p.timeA, pontos.get(p.timeA) + 3);
            } else if (p.golsA < p.golsB) {
                pontos.put(p.timeB, pontos.get(p.timeB) + 3);
            } else {
                pontos.put(p.timeA, pontos.get(p.timeA) + 1);
                pontos.put(p.timeB, pontos.get(p.timeB) + 1);
            }
        }

        return new ResultadoTorneio(partidas, pontos);
    }
}

class ResultadoTorneio {
    List<Partida> partidas;
    Map<String, Integer> pontos;

    ResultadoTorneio(List<Partida> partidas, Map<String, Integer> pontos) {
        this.partidas = partidas;
        this.pontos = pontos;
    }

    void imprimirClassificacao() {
        System.out.println("\nClassificação Final:");
        List<String> listaTimes = new ArrayList<>(pontos.keySet());
        Collections.sort(listaTimes, new Comparator<String>() {
            public int compare(String t1, String t2) {
                return pontos.get(t2) - pontos.get(t1);
            }
        });
        for (String time : listaTimes) {
            System.out.println(time + " (" + pontos.get(time) + " pontos)");
        }
    }

    void imprimirResultados() {
        System.out.println("\nResultados:");
        for (Partida p : partidas) {
            System.out.println(p.timeA + " " + p.golsA + " x " + p.golsB + " " + p.timeB);
        }
    }
}

public class App {
    public static void main(String[] args) {
        Torneio torneio = new Torneio();

        torneio.adicionarTime("Brasil");
        torneio.adicionarTime(""); // ❌ 
        torneio.adicionarTime("Argentina");
        torneio.adicionarTime("Angola");

        torneio.criarPartida("Brasil", "Canadá", 1, 0);
        torneio.criarPartida("Argentina", "Angola", 2, 0);
        torneio.criarPartida("Brasil", "Argentina", -10, -2); // ❌ 
        torneio.criarPartida("Brasil", "Argentina", 0, 2);
        torneio.criarPartida("Angola", "Canadá", 1, 1);
        torneio.criarPartida("Brasil", "Angola", 3, 2);
        torneio.criarPartida("Argentina", "Nigéria", 3, 3); // ❌ 
        torneio.criarPartida("Argentina", "Canadá", 2, 4);

        ResultadoTorneio resultados = torneio.jogar();
        resultados.imprimirClassificacao();
        resultados.imprimirResultados();
    }
}
