package conexionbd_transacciones;

import javax.swing.JOptionPane;

/**
 * Proyecto donde se presentan las transacciones junto con el JOptionPane.
 * Unidad 14.
 *
 * @author Angonoa Franco
 * @since Junio 2020
 * @version 1.0
 */
public class ConexionBD_Transacciones {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Creamos un objecto Connect con el patron Singleton.
        Connect con = Connect.getInstancia();

        menuDeOpciones(con);
    }

    public static void menuDeOpciones(Connect con) {
        int nroOpcion;

        do {
            String menu = "1 - Transferencia SIN transacción";
            menu += "\n2 - Transferencia CON transacción";
            menu += "\n3 - Mostrar datos de las cuentas";
            menu += "\n0 - Salir";
            String opcion = JOptionPane.showInputDialog(null, menu, "Bienvenidos", JOptionPane.INFORMATION_MESSAGE);
            nroOpcion = Integer.parseInt(opcion);

            switch (nroOpcion) {
                case 1:
                    con.transferirSINTransaccion(solicitarTransferncia());
                    break;
                case 2:
                    con.transferirCONTransaccion(solicitarTransferncia());
                    break;
                case 3:
                    String datos = con.consultar();
                    JOptionPane.showMessageDialog(null, datos, "Datos: ", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "¡Hasta luego!");
            }

        } while (nroOpcion != 0);
    }

    public static int solicitarTransferncia() {
        String transferencia = JOptionPane.showInputDialog(null, "Transferencia", "¿Cuánto va a transferir?", JOptionPane.QUESTION_MESSAGE);
        return Integer.parseInt(transferencia);
    }
}
