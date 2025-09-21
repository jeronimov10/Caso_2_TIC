package app;

public class EntradaTabla {
    private boolean cargada;
    private int marcoFisico;
    private long ultimoUso;

    public boolean isCargada() { return cargada; }
    public void setCargada(boolean v) { cargada = v; }
    public int getMarcoFisico() { return marcoFisico; }
    public void setMarcoFisico(int v) { marcoFisico = v; }
    public long getUltimoUso() { return ultimoUso; }
    public void setUltimoUso(long v) { ultimoUso = v; }
}

