package com.uerj.gymflow.model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private Integer idFuncionario;
    private String cargo;
    private LocalDate dataAdmissao;
    private Float salario;

    public Funcionario() {
    }

    public Funcionario(String cargo, LocalDate dataAdmissao, Float salario) {
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Float getSalario() {
        return salario;
    }

    public void setSalario(Float salario) {
        this.salario = salario;
    }
}
