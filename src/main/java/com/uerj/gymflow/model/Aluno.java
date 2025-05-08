package com.uerj.gymflow.model;

import java.time.LocalDate;

public class Aluno extends Pessoa {
    private Integer idAluno;
    private LocalDate dataMatricula;
    private LocalDate dataAssinatura;
    private Integer idPlano;
    private Plano plano;
    
    public Aluno() {
    }
    
    public Aluno(String nome, LocalDate dataNascimento, String cpf, String telefone, String email,
                LocalDate dataMatricula, LocalDate dataAssinatura, Integer idPlano) {
        super(nome, dataNascimento, cpf, telefone, email);
        this.dataMatricula = dataMatricula;
        this.dataAssinatura = dataAssinatura;
        this.idPlano = idPlano;
    }
    
    public Integer getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
    }
    
    public LocalDate getDataMatricula() {
        return dataMatricula;
    }
    
    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }
    
    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }
    
    public void setDataAssinatura(LocalDate dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }
    
    public Integer getIdPlano() {
        return idPlano;
    }
    
    public void setIdPlano(Integer idPlano) {
        this.idPlano = idPlano;
    }
    
    public Plano getPlano() {
        return plano;
    }
    
    public void setPlano(Plano plano) {
        this.plano = plano;
    }
    
    @Override
    public String toString() {
        return "Aluno{" +
                "idAluno=" + idAluno +
                ", idPessoa=" + getIdPessoa() +
                ", nome='" + getNome() + '\'' +
                ", cpf='" + getCpf() + '\'' +
                ", dataMatricula=" + dataMatricula +
                ", dataAssinatura=" + dataAssinatura +
                ", idPlano=" + idPlano +
                '}';
    }
}