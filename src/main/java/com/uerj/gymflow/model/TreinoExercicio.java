package com.uerj.gymflow.model;

public class TreinoExercicio {
    private Integer idTreino;
    private Integer idExercicio;
    private Integer repeticoes;
    private Integer series;
    private Float carga;
    private Integer ordem;
    private String observacoes;
    
    private Treino treino;
    private Exercicio exercicio;

    public TreinoExercicio() {
    }

    public TreinoExercicio(Integer idTreino, Integer idExercicio, Integer repeticoes, Integer series, 
                          Float carga, Integer ordem, String observacoes) {
        this.idTreino = idTreino;
        this.idExercicio = idExercicio;
        this.repeticoes = repeticoes;
        this.series = series;
        this.carga = carga;
        this.ordem = ordem;
        this.observacoes = observacoes;
    }

    public Integer getIdTreino() {
        return idTreino;
    }

    public void setIdTreino(Integer idTreino) {
        this.idTreino = idTreino;
    }

    public Integer getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(Integer idExercicio) {
        this.idExercicio = idExercicio;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Float getCarga() {
        return carga;
    }

    public void setCarga(Float carga) {
        this.carga = carga;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
        if (exercicio != null) {
            this.idExercicio = exercicio.getIdExercicio();
        }
    }

    @Override
    public String toString() {
        return "TreinoExercicio{" +
                "idTreino=" + idTreino +
                ", idExercicio=" + idExercicio +
                ", repeticoes=" + repeticoes +
                ", series=" + series +
                ", carga=" + carga +
                ", ordem=" + ordem +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
