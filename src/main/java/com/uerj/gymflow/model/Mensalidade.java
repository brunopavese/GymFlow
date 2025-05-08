package com.uerj.gymflow.model;

import java.time.LocalDate;

public class Mensalidade {
    private Integer idMensalidade;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private Float valorPago;
    private String statusPagamento;
    private Integer idPlano;
    private Plano plano;
    private Integer idAluno;
    private Aluno aluno;

    public Mensalidade() {
    }

    public Mensalidade(LocalDate dataVencimento, Float valorPago, Integer idPlano, Integer idAluno) {
        this.dataVencimento = dataVencimento;
        this.valorPago = valorPago;
        this.idPlano = idPlano;
        this.idAluno = idAluno;
        this.statusPagamento = "Pendente";
    }

    public Mensalidade(LocalDate dataVencimento, LocalDate dataPagamento, Float valorPago, 
                      String statusPagamento, Integer idPlano, Integer idAluno) {
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.statusPagamento = statusPagamento;
        this.idPlano = idPlano;
        this.idAluno = idAluno;
    }

    public Integer getIdMensalidade() {
        return idMensalidade;
    }

    public void setIdMensalidade(Integer idMensalidade) {
        this.idMensalidade = idMensalidade;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Float getValorPago() {
        return valorPago;
    }

    public void setValorPago(Float valorPago) {
        this.valorPago = valorPago;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
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
        if (plano != null) {
            this.idPlano = plano.getIdPlano();
        }
    }

    public Integer getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Integer idAluno) {
        this.idAluno = idAluno;
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

    /**
     * Registra o pagamento da mensalidade
     * @param dataPagamento Data em que o pagamento foi realizado
     * @param valorPago Valor pago
     */
    public void registrarPagamento(LocalDate dataPagamento, Float valorPago) {
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.statusPagamento = "Pago";
    }

    /**
     * Verifica se a mensalidade está atrasada
     * @return true se estiver atrasada, false caso contrário
     */
    public boolean isAtrasada() {
        if ("Pago".equals(statusPagamento)) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }

    @Override
    public String toString() {
        return "Mensalidade{" +
                "idMensalidade=" + idMensalidade +
                ", dataVencimento=" + dataVencimento +
                ", dataPagamento=" + dataPagamento +
                ", valorPago=" + valorPago +
                ", statusPagamento='" + statusPagamento + '\'' +
                ", idPlano=" + idPlano +
                ", idAluno=" + idAluno +
                '}';
    }
}
