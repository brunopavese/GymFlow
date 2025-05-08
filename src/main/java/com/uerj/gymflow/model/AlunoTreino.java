package com.uerj.gymflow.model;

import java.time.LocalDate;

public class AlunoTreino {
    private Integer idAluno;
    private Integer idTreino;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacoes;
    
    private Aluno aluno;
    private Treino treino;

    public AlunoTreino() {
        this.dataInicio = LocalDate.now();
    }

    public AlunoTreino(Integer idAluno, Integer idTreino, LocalDate dataInicio, LocalDate dataFim, String observacoes) {
        this.idAluno = idAluno;
        this.idTreino = idTreino;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
    }

    public Integer getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
    }

    public Integer getIdTreino() {
        return idTreino;
    }

    public void setIdTreino(Integer idTreino) {
        this.idTreino = idTreino;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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

    public Treino getTreino() {
        return treino;
    }

    public void setTreino(Treino treino) {
        this.treino = treino;
        if (treino != null) {
            this.idTreino = treino.getIdTreino();
        }
    }

    /**
     * Verifica se o treino está ativo na data atual
     * @return true se o treino estiver ativo
     */
    public boolean isAtivo() {
        LocalDate hoje = LocalDate.now();
        return (hoje.isEqual(dataInicio) || hoje.isAfter(dataInicio)) && 
               (dataFim == null || hoje.isBefore(dataFim) || hoje.isEqual(dataFim));
    }

    /**
     * Calcula quantos dias faltam para o treino expirar
     * @return número de dias até a expiração ou -1 se já expirou ou null se não tem data fim
     */
    public Integer diasAteExpirar() {
        if (dataFim == null) {
            return null;
        }
        
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(dataFim)) {
            return -1;
        }
        
        return (int) hoje.until(dataFim).getDays();
    }

    @Override
    public String toString() {
        return "AlunoTreino{" +
                "idAluno=" + idAluno +
                ", idTreino=" + idTreino +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
