package com.uerj.gymflow.model;

public class Exercicio {
    private Integer idExercicio;
    private String nomeExercicio;
    private String descricao;
    private String grupoMuscular;

    public Exercicio() {
    }

    public Exercicio(String nomeExercicio, String descricao, String grupoMuscular) {
        this.nomeExercicio = nomeExercicio;
        this.descricao = descricao;
        this.grupoMuscular = grupoMuscular;
    }

    public Exercicio(Integer idExercicio, String nomeExercicio, String descricao, String grupoMuscular) {
        this.idExercicio = idExercicio;
        this.nomeExercicio = nomeExercicio;
        this.descricao = descricao;
        this.grupoMuscular = grupoMuscular;
    }

    public Integer getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(Integer idExercicio) {
        this.idExercicio = idExercicio;
    }

    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    @Override
    public String toString() {
        return "Exercicio{" +
                "idExercicio=" + idExercicio +
                ", nomeExercicio='" + nomeExercicio + '\'' +
                ", descricao='" + descricao + '\'' +
                ", grupoMuscular='" + grupoMuscular + '\'' +
                '}';
    }
}
