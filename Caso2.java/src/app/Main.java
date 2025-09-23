package app;

public class Main {
    public static void main(String[] args) throws Exception {
        
        String ruta_config = "Caso2.java/src/app/config.txt";

        Opcion1.generar_desde_config(ruta_config);
        System.out.println("Opción 1 finalizada.");

        int nproc = Opcion1.leer_nproc(ruta_config);
        int marcosTotales = (args.length >= 1) ? Integer.parseInt(args[0]) : 8;
        Opcion2.simular_ejecucion(nproc, marcosTotales);
    }
}


