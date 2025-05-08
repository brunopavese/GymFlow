package com.uerj.gymflow.model;

import java.time.LocalDate;

public class Avaliacao {
    private Integer idAvaliacao;
    private LocalDate dataAvaliacao;
    private Float peso;
    private Float altura;
    private String observacoes;
    private Integer idAluno;
    private Integer idProfessor;
    
    private Aluno aluno;
    private Professor professor;

    public Avaliacao() {
        this.dataAvaliacao = LocalDate.now();
    }

    public Avaliacao(LocalDate dataAvaliacao, Float peso, Float altura, 
                     String observacoes, Integer idAluno, Integer idProfessor) {
        this.dataAvaliacao = dataAvaliacao;
        this.peso = peso;
        this.altura = altura;
        this.observacoes = observacoes;
        this.idAluno = idAluno;
        this.idProfessor = idProfessor;
    }

    public Integer getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(Integer idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public Float getAltura() {
        return altura;
    }

    public void setAltura(Float altura) {
        this.altura = altura;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Integer getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
    }

    public Integer getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        if (aluno != null) {
            this.idAluno = aluno.getIdAluno();
        }
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
        if (professor != null) {
            this.idProfessor = professor.getIdProfessor();
        }
    }
    
    /**
     * Calcula o IMC (Índice de Massa Corporal) baseado no peso e altura.
     * Este valor não é armazenado no banco de dados, apenas calculado quando necessário.
     * @return O valor do IMC ou null se peso ou altura forem nulos
     */
    public Float calcularIMC() {
        if (this.peso != null && this.altura != null && this.altura > 0) {
            // Altura deve estar em metros para o cálculo
            float alturaMetros = this.altura;
            if (alturaMetros > 3) {
                // Se altura for maior que 3, provavelmente está em centímetros
                alturaMetros = alturaMetros / 100;
            }
            return this.peso / (alturaMetros * alturaMetros);
        }
        return null;
    }
    
    /**
     * Retorna a classificação do IMC de acordo com a OMS
     * @return Classificação do IMC
     */
    public String getClassificacaoIMC() {
        Float imc = calcularIMC();
        if (imc == null) {
            return "Não calculado";
        }
        
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 25) {
            return "Peso normal";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else if (imc < 35) {
            return "Obesidade grau 1";
        } else if (imc < 40) {
            return "Obesidade grau 2";
        } else {
            return "Obesidade grau 3";
        }
    }
    
    /**
     * Verifica se houve melhoria em relação a uma avaliação anterior
     * @param avaliacaoAnterior A avaliação anterior para comparação
     * @return Um resumo das melhorias ou pioras
     */
    public String compararCom(Avaliacao avaliacaoAnterior) {
        if (avaliacaoAnterior == null) {
            return "Sem avaliação anterior para comparação.";
        }
        
        StringBuilder resultado = new StringBuilder();
        resultado.append("Comparação com avaliação de ").append(avaliacaoAnterior.getDataAvaliacao()).append(":\n");
        
        // Comparar peso
        if (this.peso != null && avaliacaoAnterior.getPeso() != null) {
            float difPeso = this.peso - avaliacaoAnterior.getPeso();
            if (Math.abs(difPeso) > 0.1f) {
                resultado.append("- Peso: ")
                        .append(difPeso > 0 ? "Aumento de " : "Redução de ")
                        .append(String.format("%.2f", Math.abs(difPeso)))
                        .append(" kg\n");
            } else {
                resultado.append("- Peso: Sem alteração significativa\n");
            }
        }
        
        // Comparar IMC (calculado, não armazenado)
        Float imcAtual = calcularIMC();
        Float imcAnterior = avaliacaoAnterior.calcularIMC();
        if (imcAtual != null && imcAnterior != null) {
            float difIMC = imcAtual - imcAnterior;
            if (Math.abs(difIMC) > 0.1f) {
                resultado.append("- IMC: ")
                        .append(difIMC > 0 ? "Aumento de " : "Redução de ")
                        .append(String.format("%.2f", Math.abs(difIMC)))
                        .append("\n");
            } else {
                resultado.append("- IMC: Sem alteração significativa\n");
            }
        }
        
        return resultado.toString();
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "idAvaliacao=" + idAvaliacao +
                ", dataAvaliacao=" + dataAvaliacao +
                ", peso=" + peso +
                ", altura=" + altura +
                ", idAluno=" + idAluno +
                ", idProfessor=" + idProfessor +
                '}';
    }
}
