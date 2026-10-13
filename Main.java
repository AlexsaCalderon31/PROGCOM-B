/******************************************************************************

                            Online Java Compiler.
                Code, Compile, Run and Debug java program online.
Write your code in this editor and press "Run" button to execute it.

*******************************************************************************/
import java.util.*;
 
public class Main {
 
    static class Flor {
        final String nombre;
        int cantidad;
        final int precio;
 
        Flor(String nombre, int cantidad, int precio) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
        }
    }
 
    record Linea(String nombre, int cantidad, int precio) {
        int subtotal() { return cantidad * precio; }
    }
 
    record Factura(List<Linea> lineas, int total, String medioPago) {}
 
    static final String[] MEDIOS_PAGO = {
        "Efectivo", "Tarjeta de crédito", "Tarjeta débito", "Otro medio de pago"
    };
 
    static final Scanner sc = new Scanner(System.in);
    static final Map<String, Flor> inventario = new LinkedHashMap<>();
    static final Map<String, Integer> carrito = new LinkedHashMap<>();
    static Factura ultimaFactura = null;
 
    public static void main(String[] args) {
        inventario.put("girasol",   new Flor("girasol", 50, 5000));
        inventario.put("lirio",     new Flor("lirio", 30, 23000));
        inventario.put("rosa",      new Flor("rosa", 80, 3000));
        inventario.put("tulipán",   new Flor("tulipán", 40, 4500));
        inventario.put("orquídea",  new Flor("orquídea", 10, 35000));
        inventario.put("margarita", new Flor("margarita", 60, 2000));
 
        System.out.println("Bienvenido a mi floristería\n");
        System.out.println("Menú");
 
        boolean salir = false;
        while (!salir) {
            System.out.println("¿Qué desea hacer?");
            System.out.println("1. Visualizar nuestras flores");
            System.out.println("2. Comprar");
            System.out.println("3. Pagar");
            System.out.println("4. Factura de compra");
            System.out.println("5. Salir");
 
            int opc = leerEntero("Ingrese su opción\n", 1, 5);
            switch (opc) {
                case 1 -> mostrarInventario();
                case 2 -> comprar();
                case 3 -> pagar();
                case 4 -> mostrarFactura();
                case 5 -> {
                    System.out.println("Gracias por su compra");
                    salir = true;
                }
            }
        }
    }
 
    // Pide un entero y repite hasta que sea válido y esté dentro del rango.
    static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                int valor = Integer.parseInt(linea);
                if (valor < min || valor > max) {
                    System.out.printf("Ingrese un valor entre %d y %d.%n", min, max);
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }
 
    static String capitalizar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
 
    static void mostrarInventario() {
        System.out.println("\nEstas son nuestras flores");
        System.out.printf("%-15s-%-10s-%-15s%n", "Flor", "Cantidad", "Precio unit");
        for (Flor f : inventario.values()) {
            System.out.printf("%-15s-%-10d-$%-14d%n", capitalizar(f.nombre), f.cantidad, f.precio);
        }
        System.out.println();
    }
 
    static int totalCarrito() {
        int total = 0;
        for (Map.Entry<String, Integer> e : carrito.entrySet()) {
            total += e.getValue() * inventario.get(e.getKey()).precio;
        }
        return total;
    }
 
    static void mostrarCarrito() {
        System.out.println("\nCarrito");
        if (carrito.isEmpty()) {
            System.out.println("No hay nada en el carrito :(\n");
            return;
        }
        for (Map.Entry<String, Integer> e : carrito.entrySet()) {
            int precio = inventario.get(e.getKey()).precio;
            System.out.printf("- %-12s x%-4d $%d%n",
                    capitalizar(e.getKey()), e.getValue(), e.getValue() * precio);
        }
        System.out.printf("Total parcial: $%d%n%n", totalCarrito());
    }
 
    static void comprar() {
        List<String> nombres = new ArrayList<>(inventario.keySet());
        while (true) {
            mostrarInventario();
            mostrarCarrito();
            for (int i = 0; i < nombres.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, capitalizar(nombres.get(i)));
            }
            int sel = leerEntero("Elija una flor (0 para volver al menú): ", 0, nombres.size());
            if (sel == 0) return;
 
            String nombre = nombres.get(sel - 1);
            int disponible = inventario.get(nombre).cantidad - carrito.getOrDefault(nombre, 0);
            if (disponible <= 0) {
                System.out.println("No hay más unidades disponibles de esa flor.\n");
                continue;
            }
            int cant = leerEntero(
                    "Cantidad (disponible " + disponible + ", 0 cancela): ", 0, disponible);
            if (cant > 0) {
                carrito.merge(nombre, cant, Integer::sum);
                System.out.printf("Se agregaron %d %s(s) al carrito.%n%n", cant, nombre);
            }
        }
    }
 
    static void pagar() {
        if (carrito.isEmpty()) {
            System.out.println("\nEl carrito está vacío. Compre algo primero.\n");
            return;
        }
 
        int total = totalCarrito();
        System.out.printf("%nUsted debe pagar $%d%n", total);
        System.out.println("Seleccione el medio de pago");
        for (int i = 0; i < MEDIOS_PAGO.length; i++) {
            System.out.printf("%d. %s%n", i + 1, MEDIOS_PAGO[i]);
        }
        int opc = leerEntero("Seleccione su opción (0 para cancelar): ", 0, MEDIOS_PAGO.length);
        if (opc == 0) {
            System.out.println("Pago cancelado.\n");
            return;
        }
 
        List<Linea> lineas = new ArrayList<>();
        for (Map.Entry<String, Integer> e : carrito.entrySet()) {
            Flor f = inventario.get(e.getKey());
            f.cantidad -= e.getValue();   // descontar del inventario
            lineas.add(new Linea(f.nombre, e.getValue(), f.precio));
        }
 
        ultimaFactura = new Factura(lineas, total, MEDIOS_PAGO[opc - 1]);
        carrito.clear();
        System.out.println("Pago realizado con éxito.\n");
    }
 
    static void mostrarFactura() {
        if (ultimaFactura == null) {
            System.out.println("\nAún no hay una compra pagada para facturar.\n");
            return;
        }
        System.out.println("\n**FACTURA DE COMPRA**");
        System.out.printf("%-15s%-8s%-12s%-12s%n", "Producto", "Cant.", "P. unit", "Subtotal");
        for (Linea l : ultimaFactura.lineas()) {
            System.out.printf("%-15s%-8d$%-11d$%-11d%n",
                    capitalizar(l.nombre()), l.cantidad(), l.precio(), l.subtotal());
        }
        System.out.printf("%nTotal: $%d%n", ultimaFactura.total());
        System.out.println("(SOMOS EVASORES DE IMPUESTOS (:))");
        System.out.printf("Método de pago: %s%n%n", ultimaFactura.medioPago());
    }
}
 