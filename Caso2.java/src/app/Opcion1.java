package app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Opcion1 {
    static final int BYTES_ENTERO = 4;

    public static void generar_desde_config(String ruta_config) throws Exception {
        Map<String, String> m = leer_config(ruta_config);
        int tp = Integer.parseInt(m.get("TP"));
        int nproc = Integer.parseInt(m.get("NPROC"));
        int[][] tamanos = parsear_tamanos(m.get("TAMS"));
        if (tamanos.length != nproc) throw new RuntimeException("NPROC y TAMS difieren");
        for (int i = 0; i < nproc; i++) {
            int nf = tamanos[i][0];
            int nc = tamanos[i][1];
            generar_archivo("proc" + i + ".txt", tp, nf, nc);
        }
    }

    public static int leer_nproc(String ruta_config) throws Exception {
        Map<String, String> m = leer_config(ruta_config);
        return Integer.parseInt(m.get("NPROC"));
    }

    static Map<String, String> leer_config(String ruta) throws Exception {
        File f = new File(ruta);
        if (!f.exists()) {
            throw new FileNotFoundException("El archivo config.txt no está en la posición esperada: " + ruta);
        }

        Map<String, String> out = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty() || !s.contains("=")) continue;
                int p = s.indexOf('=');
                String k = s.substring(0, p).trim().toUpperCase(Locale.ROOT);
                String v = s.substring(p + 1).trim();
                out.put(k, v);
            }
        }

        if (!out.containsKey("TP") || !out.containsKey("NPROC") || !out.containsKey("TAMS"))
            throw new RuntimeException("Config incompleta: TP, NPROC y TAMS requeridos");

        return out;
    }

    static int[][] parsear_tamanos(String tams) {
        String[] partes = tams.split(",");
        int[][] out = new int[partes.length][2];
        for (int i = 0; i < partes.length; i++) {
            String s = partes[i].trim().toLowerCase(Locale.ROOT);
            int x = s.indexOf('x');
            out[i][0] = Integer.parseInt(s.substring(0, x).trim());
            out[i][1] = Integer.parseInt(s.substring(x + 1).trim());
        }
        return out;
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
                    long off = idx * BYTES_ENTERO;
                    escribir_ref(bw, "M1", i, j, tp, base_m1 + off, 'r');
                    escribir_ref(bw, "M2", i, j, tp, base_m2 + off, 'r');
                    escribir_ref(bw, "M3", i, j, tp, base_m3 + off, 'w');
                }
            }
        }
    }

    static void escribir_ref(BufferedWriter bw, String m, int i, int j, int tp, long dir, char rw) throws IOException {
        long pag = dir / tp;
        long desp = dir % tp;
        bw.write(m + ":[" + i + "-" + j + "]," + pag + "," + desp + "," + rw + "\n");
    }
}


