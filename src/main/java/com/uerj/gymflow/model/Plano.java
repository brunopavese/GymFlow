package com.uerj.gymflow.model;

public class Plano {
    private Integer idPlano;
    private String nomePlano;
    private String descricao;
    private Integer duracao;
    private Float valorMensal;

    public Plano() {
    }

    public Plano(String nomePlano, String descricao, Integer duracao, Float valorMensal) {
        this.nomePlano = nomePlano;
        this.descricao = descricao;
        this.duracao = duracao;
        this.valorMensal = valorMensal;
    }

    public Integer getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(Integer idPlano) {
        this.idPlano = idPlano;
    }

    public String getNomePlano() {
        return nomePlano;
    }

    public void setNomePlano(String nomePlano) {
        this.nomePlano = nomePlano;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Float getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(Float valorMensal) {
        this.valorMensal = valorMensal;
    }

    @Override
    public String toString() {
        return "Plano{" +
                "idPlano=" + idPlano +
                ", nomePlano='" + nomePlano + '\'' +
                ", descricao='" + descricao + '\'' +
                ", duracao=" + duracao +
                ", valorMensal=" + valorMensal +
                '}';
    }

    public Float calcularValorTotal() {
        return this.duracao * this.valorMensal;
    }
}
