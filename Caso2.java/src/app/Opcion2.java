package app;

import java.util.*;

public class Opcion2 {
    public static void simular_ejecucion(int num_procesos, int marcos_totales) {
        System.out.println("=== INICIO DE LA SIMULACIÓN ===");

        var procesos = new ArrayList<Proceso>();
        var cola = new ArrayDeque<Proceso>();

        for (int i = 0; i < num_procesos; i++) {
            var p = new Proceso(i, "proc" + i + ".txt");
            procesos.add(p);
            cola.add(p);

            System.out.println("PROC " + i + " == Leyendo archivo de configuración ==");
            System.out.println("PROC " + i + "leyendo TP. Tam Páginas: " + p.get_tamano_pagina());
            System.out.println("PROC " + i + "leyendo NF. Num Filas: " + p.get_nf());
            System.out.println("PROC " + i + "leyendo NC. Num Cols: " + p.get_nc());
            System.out.println("PROC " + i + "leyendo NR. Num Referencias: " + p.get_total_referencias());
            System.out.println("PROC " + i + "leyendo NP. Num Paginas: " + p.get_np());
            System.out.println("PROC " + i + "== Terminó de leer archivo de configuración ==");
        }

        int marcos_por_proceso = marcos_totales / num_procesos;
        int siguiente_marco = 0;
        for (var p : procesos) {
            for (int j = 0; j < marcos_por_proceso; j++) {
                p.agregar_marco_propio(siguiente_marco);
                System.out.println("Proceso " + p.get_id() + ": recibe marco " + siguiente_marco);
                siguiente_marco++;
            }
        }

        long reloj = 1;

        while (!cola.isEmpty()) {
            var pr = cola.poll();

            if (!pr.tiene_mas_referencias()) {
                System.out.println("========================  ");
                System.out.println("Termino proc: " + pr.get_id());
                System.out.println("======================== ");
                var libres = pr.liberar_todos_los_marcos();
                for (int id : libres) System.out.println("PROC " + pr.get_id() + " removiendo marco: " + id);

                var candidato = procesos.stream()
                        .filter(x -> x.tiene_mas_referencias())
                        .max(Comparator.comparingInt(Proceso::get_fallos))
                        .orElse(null);

                if (candidato != null) {
                    for (int id : libres) {
                        candidato.agregar_marco_propio(id);
                        System.out.println("PROC " + candidato.get_id() + " asignando marco nuevo " + id);
                    }
                }
                continue;
            }

            System.out.println("Turno proc: " + pr.get_id());
            System.out.println("PROC " + pr.get_id() + " analizando linea_: " + pr.get_indice_actual());

            int pag_virtual = pr.obtener_siguiente_pagina_virtual();
            var entrada = pr.obtener_entrada(pag_virtual);

            if (entrada.isCargada()) {
                entrada.setUltimoUso(reloj++);
                pr.inc_hits();
                System.out.println("PROC " + pr.get_id() + " hits: " + pr.get_hits());
                System.out.println("PROC " + pr.get_id() + " envejecimiento");
                pr.avanzar_indice();
                if (pr.tiene_mas_referencias()) cola.add(pr);
            } else {
                pr.inc_fallos();

                var usados = new HashSet<Integer>();
                for (var e : pr.get_tabla_paginas().values()) if (e.isCargada()) usados.add(e.getMarcoFisico());

                Integer marco_libre = null;
                for (Integer m : pr.get_marcos_propios()) if (!usados.contains(m)) { marco_libre = m; break; }

                if (marco_libre != null) {
                    entrada.setCargada(true);
                    entrada.setMarcoFisico(marco_libre);
                    entrada.setUltimoUso(reloj++);
                    pr.inc_swap(1);
                    System.out.println("PROC " + pr.get_id() + " falla de pag: " + pr.get_fallos());
                    System.out.println("PROC " + pr.get_id() + " envejecimiento");
                    cola.add(pr);
                } else {
                    var victima = pr.get_tabla_paginas().values().stream()
                            .filter(app.EntradaTabla::isCargada)
                            .min(Comparator.comparingLong(app.EntradaTabla::getUltimoUso))
                            .orElse(null);

                    if (victima != null) {
                        int marco = victima.getMarcoFisico();
                        victima.setCargada(false);
                        entrada.setCargada(true);
                        entrada.setMarcoFisico(marco);
                        entrada.setUltimoUso(reloj++);
                        pr.inc_swap(2);
                    }
                    System.out.println("PROC " + pr.get_id() + " falla de pag: " + pr.get_fallos());
                    System.out.println("PROC " + pr.get_id() + " reemplazando página (LRU)");
                    System.out.println("PROC " + pr.get_id() + " envejecimiento");
                    cola.add(pr);
                }
            }
        }

        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        for (var p : procesos) {
            System.out.println("Proceso: " + p.get_id());
            System.out.println("- Num referencias: " + p.get_total_referencias());
            System.out.println("- Fallas: " + p.get_fallos());
            System.out.println("- Hits: " + p.get_hits());
            System.out.println("- SWAP: " + p.get_accesos_swap());
            double tasa_fallos = p.get_total_referencias() == 0 ? 0 : (double)p.get_fallos()/p.get_total_referencias();
            double tasa_exito  = p.get_total_referencias() == 0 ? 0 : (double)p.get_hits()/p.get_total_referencias();
            System.out.printf("- Tasa fallas: %.4f%n", tasa_fallos);
            System.out.printf("- Tasa éxito: %.4f%n", tasa_exito);
        }

        System.out.println("=== FIN DE LA SIMULACIÓN ===");
    }
}

