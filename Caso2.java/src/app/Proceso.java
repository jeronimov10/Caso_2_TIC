package app;

import java.io.*;
import java.util.*;

public class Proceso {
    private final int id;
    private int tamano_pagina;
    private int nf;
    private int nc;
    private int total_referencias;
    private int np;
    private int indice_actual;

    private final List<Integer> paginas_virtuales;
    private final Map<Integer, EntradaTabla> tabla_paginas;

    private final LinkedHashSet<Integer> marcos_propios;
    private int fallos;
    private int accesos_swap;
    private int hits;

    public Proceso(int id, String archivo) {
        this.id = id;
        this.paginas_virtuales = new ArrayList<>();
        this.tabla_paginas = new HashMap<>();
        this.marcos_propios = new LinkedHashSet<>();
        this.indice_actual = 0;
        cargar_desde_archivo(archivo);
    }

    void cargar_desde_archivo(String archivo) {
        try (var br = new BufferedReader(new FileReader(archivo))) {
            this.tamano_pagina     = Integer.parseInt(br.readLine().split("=")[1]);
            this.nf                = Integer.parseInt(br.readLine().split("=")[1]);
            this.nc                = Integer.parseInt(br.readLine().split("=")[1]);
            this.total_referencias = Integer.parseInt(br.readLine().split("=")[1]);
            this.np                = Integer.parseInt(br.readLine().split("=")[1]);

            for (int i = 0; i < total_referencias; i++) {
                String linea = br.readLine();
                if (linea == null) break;
                String[] partes = linea.split(",");
                int paginaVirtual = Integer.parseInt(partes[1]);
                this.paginas_virtuales.add(paginaVirtual);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo " + archivo, e);
        }
    }

    public boolean tiene_mas_referencias() { return indice_actual < total_referencias; }
    public int obtener_siguiente_pagina_virtual() { return paginas_virtuales.get(indice_actual); }
    public void avanzar_indice() { indice_actual++; }
    public int get_indice_actual() { return indice_actual; }

    public EntradaTabla obtener_entrada(int pv) { return tabla_paginas.computeIfAbsent(pv, k -> new EntradaTabla()); }
    public Map<Integer, EntradaTabla> get_tabla_paginas() { return tabla_paginas; }

    public void agregar_marco_propio(int id) { marcos_propios.add(id); }
    public List<Integer> liberar_todos_los_marcos() { var l = new ArrayList<>(marcos_propios); marcos_propios.clear(); return l; }
    public Set<Integer> get_marcos_propios() { return marcos_propios; }

    public int get_id() { return id; }
    public int get_fallos() { return fallos; }
    public void inc_fallos() { fallos++; }
    public int get_accesos_swap() { return accesos_swap; }
    public void inc_swap(int c) { accesos_swap += c; }
    public int get_hits() { return hits; }
    public void inc_hits() { hits++; }
    public int get_total_referencias() { return total_referencias; }
    public int get_nf() { return nf; }
    public int get_nc() { return nc; }
    public int get_np() { return np; }
    public int get_tamano_pagina() { return tamano_pagina; }
}

