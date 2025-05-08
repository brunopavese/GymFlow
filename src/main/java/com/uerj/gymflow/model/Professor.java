package com.uerj.gymflow.model;

import java.time.LocalDate;

public class Professor extends Funcionario {
    private Integer idProfessor;
    private String especialidade;
    private String cref;

    public Professor() {
    }

    public Professor(String especialidade, String cref) {
        this.especialidade = especialidade;
        this.cref = cref;
    }

    public Professor(String cargo, LocalDate dataAdmissao, Float salario,
                     String especialidade, String cref) {
        super(cargo, dataAdmissao, salario);
        this.especialidade = especialidade;
        this.cref = cref;
    }

    public Integer getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "idProfessor=" + idProfessor +
                ", especialidade='" + especialidade + '\'' +
                ", cref='" + cref + '\'' +
                ", idFuncionario=" + getIdFuncionario() +
                ", cargo='" + getCargo() + '\'' +
                ", nome='" + getNome() + '\'' +
                '}';
    }
}
