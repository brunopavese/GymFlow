package com.uerj.gymflow.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Treino {
    private Integer idTreino;
    private String nomeTreino;
    private LocalDate dataCriacao;
    private String observacoes;
    private Integer idProfessor;
    private Professor professor;
    private List<Exercicio> exercicios;

    public Treino() {
        this.exercicios = new ArrayList<>();
        this.dataCriacao = LocalDate.now();
    }

    public Treino(String nomeTreino, String observacoes, Integer idProfessor) {
        this.nomeTreino = nomeTreino;
        this.dataCriacao = LocalDate.now();
        this.observacoes = observacoes;
        this.idProfessor = idProfessor;
        this.exercicios = new ArrayList<>();
    }

    public Treino(Integer idTreino, String nomeTreino, LocalDate dataCriacao, String observacoes, Integer idProfessor) {
        this.idTreino = idTreino;
        this.nomeTreino = nomeTreino;
        this.dataCriacao = dataCriacao;
        this.observacoes = observacoes;
        this.idProfessor = idProfessor;
        this.exercicios = new ArrayList<>();
    }

    public Integer getIdTreino() {
        return idTreino;
    }

    public void setIdTreino(Integer idTreino) {
        this.idTreino = idTreino;
    }

    public String getNomeTreino() {
        return nomeTreino;
    }

    public void setNomeTreino(String nomeTreino) {
        this.nomeTreino = nomeTreino;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Integer getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
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

    public List<Exercicio> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<Exercicio> exercicios) {
        this.exercicios = exercicios;
    }

    public void adicionarExercicio(Exercicio exercicio) {
        if (this.exercicios == null) {
            this.exercicios = new ArrayList<>();
        }
        this.exercicios.add(exercicio);
    }

    public void removerExercicio(Exercicio exercicio) {
        if (this.exercicios != null) {
            this.exercicios.remove(exercicio);
        }
    }

    @Override
    public String toString() {
        return "Treino{" +
                "idTreino=" + idTreino +
                ", nomeTreino='" + nomeTreino + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", observacoes='" + observacoes + '\'' +
                ", idProfessor=" + idProfessor +
                '}';
    }
}
