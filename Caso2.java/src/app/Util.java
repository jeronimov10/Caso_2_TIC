package app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Util {
    public static class Config { public int tp; public int nproc; public String tams; }

    public static Config leer_config(String ruta) throws Exception {
        var m = new HashMap<String,String>();
        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty() || !s.contains("=")) continue;
                int k = s.indexOf('=');
                m.put(s.substring(0,k).trim().toUpperCase(Locale.ROOT), s.substring(k+1).trim());
            }
        }
        var c = new Config();
        c.tp = Integer.parseInt(m.get("TP"));
        c.nproc = Integer.parseInt(m.get("NPROC"));
        c.tams = m.get("TAMS");
        return c;
    }

    public static java.util.List<int[]> parsear_tamanos(String tams) {
        var out = new java.util.ArrayList<int[]>();
        for (String p : tams.split(",")) {
            String[] a = p.trim().toLowerCase(Locale.ROOT).split("x");
            out.add(new int[]{Integer.parseInt(a[0].trim()), Integer.parseInt(a[1].trim())});
        }
        return out;
    }
}

