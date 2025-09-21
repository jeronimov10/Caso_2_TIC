package app;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Opcion1 {
    static final int BYTES_ENTERO = 4;

    public static void generar_archivos(int tp, String tams) throws Exception {
        var tamanhos = Util.parsear_tamanos(tams);
        for (int i = 0; i < tamanhos.size(); i++) {
            int nf = tamanhos.get(i)[0], nc = tamanhos.get(i)[1];
            generar_archivo("proc" + i + ".txt", tp, nf, nc);
        }
    }

    public static void generar_archivos_desde_config(String ruta_config) throws Exception {
        var cfg = Util.leer_config(ruta_config);
        var tamanhos = Util.parsear_tamanos(cfg.tams);
        if (tamanhos.size() != cfg.nproc) throw new RuntimeException("NPROC y TAMS difieren");
        for (int i = 0; i < cfg.nproc; i++) {
            int nf = tamanhos.get(i)[0], nc = tamanhos.get(i)[1];
            generar_archivo("proc" + i + ".txt", cfg.tp, nf, nc);
        }
    }

    static void generar_archivo(String nombre, int tp, int nf, int nc) throws IOException {
        long elems = 1L * nf * nc;
        long bytes_matriz = elems * BYTES_ENTERO;
        long total = bytes_matriz * 3;
        long nr = elems * 3;
        long np = (total + tp - 1) / tp;

        long base_m1 = 0;
        long base_m2 = base_m1 + bytes_matriz;
        long base_m3 = base_m2 + bytes_matriz;

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nombre), StandardCharsets.UTF_8))) {
            bw.write("TP=" + tp + "\n");
            bw.write("NF=" + nf + "\n");
            bw.write("NC=" + nc + "\n");
            bw.write("NR=" + nr + "\n");
            bw.write("NP=" + np + "\n");

            for (int i = 0; i < nf; i++) {
                long base_fila = 1L * i * nc;
                for (int j = 0; j < nc; j++) {
                    long idx = base_fila + j;
                    long desplaz = idx * BYTES_ENTERO;

                    escribir_ref(bw, "M1", i, j, tp, base_m1 + desplaz, 'r');
                    escribir_ref(bw, "M2", i, j, tp, base_m2 + desplaz, 'r');
                    escribir_ref(bw, "M3", i, j, tp, base_m3 + desplaz, 'w');
                }
            }
        }
    }

    static void escribir_ref(BufferedWriter bw, String matriz, int i, int j, int tp, long dir, char accion) throws IOException {
        long pagina = dir / tp, desp = dir % tp;
        bw.write(matriz + ":[" + i + "-" + j + "]," + pagina + "," + desp + "," + accion + "\n");
    }
}

