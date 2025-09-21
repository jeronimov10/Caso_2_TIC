package app;

public class Main {
    public static void main(String[] args) throws Exception {
        int tp = 128;
        String tams = "4x4,8x8";
        int nproc = 2;
        int marcos_totales = 8;

        Opcion1.generar_archivos(tp, tams);          
        Opcion2.simular_ejecucion(nproc, marcos_totales); 
    }
}
