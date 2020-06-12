package conexionbd_transacciones;

import java.sql.*;

/**
 * Clase donde implementa las consultas a la base de datos. Se utilizan dos
 * métodos para realizar con transacciones o sin transacciones. Los otros
 * métodos solo son de ayuda.
 *
 * @author Angonoa Franco
 * @since Junio 2020
 * @version 1.0
 */
public class Connect {

    private static final Connect INSTANCIA = new Connect();
    private static final String URL = "jdbc:sqlite:./connect/banco.sqlite";

    /**
     * Constructor para el patrón Singleton
     */
    private Connect() {

    }

    public static Connect getInstancia() {
        return INSTANCIA;
    }

    /**
     * Método que devuelve una conexión a la base de datos.
     *
     * @return objeto de tipo Connection.
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Método que cierra el recurso de un ResultSet.
     *
     * @param rs
     */
    public void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            System.err.printf("Error al cerrar ResultSet %s%n", ex);
        }
    }

    /**
     * Método que cierra el recurso de un Statement.
     *
     * @param stmt
     */
    public void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            System.err.printf("Error al cerrar Statement %s%n", ex);
        }
    }

    /**
     * Método que cierra el recurso de un Connection
     *
     * @param conn
     */
    public void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.err.printf("Error al cerrar Connection %s%n", ex);
        }
    }

    /**
     * Transferencia de dinero con transacciones. Se utiliza el
     * setAutoCommit(false), el commit() y por algún error el rollback().
     *
     * @param transferencia cantidad de dinero a transferir.
     */
    public void transferirCONTransaccion(int transferencia) {
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); //Desactivamos las consultas por cada sentencia
            int retiro = getSaldoCuenta(conn, 1) - transferencia;
            int ingreso = getSaldoCuenta(conn, 2) + transferencia;

            //Hay más de una sentencia para aplicar, entonces usamos transacciones
            String retiroQuery = "UPDATE Cuentas SET saldo=? WHERE id=1;";
            String ingresoQuery = "UPDATE Cuentas SET saldo=? WHERE id=2;";

            pStmt = conn.prepareStatement(retiroQuery);
            pStmt.setInt(1, retiro);
            pStmt.executeUpdate();

            pStmt = conn.prepareStatement(ingresoQuery);
            pStmt.setInt(1, ingreso);
            pStmt.executeUpdate();
            conn.commit(); //Si todo sale bien, devolver una confirmación.

        } catch (SQLException ex) {
            System.err.printf("Error en SQL: %s%n", ex);
            try {
                if (conn != null) {
                    System.err.printf("Falla en la operación. Rollback %n");
                    conn.rollback(); //Si algo salio mal, descartar cambios con rollback.
                }
            } catch (SQLException ex1) {
                System.out.println("Error en el rollback: " + ex1);
            }

        } finally {
            try {
                //Orden de cierre: Statement, Connect.
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                close(pStmt);
                close(conn);
            } catch (SQLException ex) {
                System.out.println("Error en autocommit: " + ex);
            }
        }

    }

    /**
     * Transferir SIN el uso de transacciones. Puede provocar errores, sumado a
     * que en cada sentencia realiza una confirmación.
     *
     * @param transferencia
     */
    public void transferirSINTransaccion(int transferencia) {
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = getConnection();
            int retiro = getSaldoCuenta(conn, 1) - transferencia;
            int ingreso = getSaldoCuenta(conn, 2) + transferencia;
            String retiroQuery = "UPDATE Cuentas SET saldo=? WHERE id=1;";
            String ingresoQuery = "UPDATE Cuentas SET saldo=? WHERE id=2;";

            pStmt = conn.prepareStatement(retiroQuery);
            pStmt.setInt(1, retiro);
            pStmt.executeUpdate();

            pStmt = conn.prepareStatement(ingresoQuery);
            pStmt.setInt(1, ingreso);
            pStmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.printf("Error en SQL: %s%n", ex);
        } finally {
            //Orden de cierre: Statement, Connect.                
            close(pStmt);
            close(conn);
        }

    }

    public int getSaldoCuenta(Connection conn, int idCuenta) {
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int saldo = 0;
        String query = "SELECT saldo FROM Cuentas WHERE id = ?;";

        try {
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, idCuenta);
            rs = pStmt.executeQuery();

            rs.next(); //Pasa de a la primer filla ya que arranca con null.

            saldo = rs.getInt(1);
        } catch (SQLException sql) {
            System.err.printf("Error de SQL: %s%n", sql);
        } finally {
            //Orden de cierre: ResultSet, Statement.
            close(rs);
            close(pStmt);
        }

        return saldo;
    }

    public String consultar() {
        String query = "SELECT * FROM Cuentas;";
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        String filas = "";

        try {
            conn = getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                int nroCuenta = rs.getInt(1);
                int saldo = rs.getInt(2);
                filas += "NroCuenta: " + nroCuenta + ", Saldo: " + saldo + "\n";
            }

        } catch (SQLException sql) {
            System.out.println("SQLException: " + sql);
        } finally {
            //Orden de cierre: ResultSet, Statement, Connection.
            close(rs);
            close(st);
            close(conn);
        }

        return filas;
    }
}
