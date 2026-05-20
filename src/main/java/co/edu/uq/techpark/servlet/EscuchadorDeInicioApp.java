package co.edu.uq.techpark.servlet;

import co.edu.uq.techpark.model.ContextoDelParque;
import co.edu.uq.techpark.model.Personal;
import co.edu.uq.techpark.model.RolPersonal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Inicializa el ContextoDelParque al arrancar la aplicación
 * y crea una cuenta de administrador por defecto para el primer uso.
 */
public class EscuchadorDeInicioApp implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ContextoDelParque contexto = ContextoDelParque.inicializar(sce.getServletContext());

        // Crear admin por defecto para que el sistema sea usable en el primer arranque
        Personal admin = new Personal();
        admin.setNombreUsuario("admin");
        admin.setHashContrasena(Personal.hashContrasena("admin123"));
        admin.setNombreCompleto("Administrador Principal");
        admin.setRol(RolPersonal.ADMINISTRADOR);
        admin.setActivo(true);
        contexto.getPersonal().insertar(admin.getNombreUsuario(), admin);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No hay recursos que liberar
    }
}
