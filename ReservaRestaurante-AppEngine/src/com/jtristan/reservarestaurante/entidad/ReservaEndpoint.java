package com.jtristan.reservarestaurante.entidad;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

@Api(name = "reservaendpoint", namespace = @ApiNamespace(ownerDomain = "jtristan.com", ownerName = "jtristan.com", packagePath = "reservarestaurante.entidad"),
clientIds={"-------------------------------.apps.googleusercontent.com",
com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID,
"---------------------------.apps.googleusercontent.com"},
audiences={"-------------------------------.apps.googleusercontent.com"})
public class ReservaEndpoint {
	
	private static final Logger log = Logger.getLogger(ReservaEndpoint.class.getName());

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
		@ApiMethod(name = "listReserva")
		public CollectionResponse<Reserva> listReserva(
				@Nullable @Named("cursor") String cursorString,
				@Nullable @Named("limit") Integer limit,
				User usuario) throws ServiceException{
	
			PersistenceManager mgr = null;
			Cursor cursor = null;
			List<Reserva> execute = null;
			
			if (usuario==null){
				String mensaje = "Se trata de un método autentificado";
				throw new UnauthorizedException(mensaje);
			}
					
		try {
			mgr = getPersistenceManager();
			
			log.warning("Usuario autentificado: " + usuario.getEmail());
			
			Query query = mgr.newQuery(Usuario.class, "cuentaGoogle == u");											
			User user = new User(usuario.getEmail(), usuario.getAuthDomain(), null);														
			query.declareParameters(Usuario.class.getName() + " u");			
			List<Usuario >usuarios = (List<Usuario>) query.execute(user);				
			
			log.warning("Encontrado Usuario autentificado: " + usuarios.size());
			
			query = mgr.newQuery(Reserva.class);
			query.setFilter("usuario == d");												
									
			query.declareVariables(Usuario.class.getName() + " d");
			
																												
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Reserva>) query.execute(usuarios.get(0));
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			log.warning("Execute size: " + execute.size());
			
			for (Reserva obj : execute){
				
				log.warning("Reserva: " + obj.getId());
				log.warning("Reserva-restaurante: " + obj.getRestaurante().getNombre());
				obj.getRestaurante();
				obj.getRestaurante().getCarta();
				obj.getRestaurante().getOfertas();
				obj.getComensales();			
				obj.getUsuario();
			}
				//;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Reserva> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getReserva")
	public Reserva getReserva(@Named("id") Long id, User usuario) {
		PersistenceManager mgr = getPersistenceManager();
		Reserva reserva = null;
		try {
			reserva = mgr.getObjectById(Reserva.class, id);
			
			Usuario usarioReserva = reserva.getUsuario();
			if (!usarioReserva.getCuentaGoogle().getEmail().equals(usuario.getEmail()) ||
					!usarioReserva.getCuentaGoogle().getAuthDomain().equals(usuario.getAuthDomain())){
				reserva = new Reserva();
			}
					
			
		} finally {
			mgr.close();
		}
		return reserva;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param reserva the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertReserva")
	public Reserva insertReserva(Reserva reserva, User usuarioOAuth) throws Exception{
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (reserva.getId()!=null){
				if (containsReserva(reserva)) {
					throw new EntityExistsException("Object already exists");
				}
			}										

			Reserva nuevaReserva = new Reserva();
			Set<Usuario>comensales = new HashSet<Usuario>();
			
			nuevaReserva.setId(reserva.getId());
			nuevaReserva.setHoraReserva(reserva.getHoraReserva());
			nuevaReserva.setNumeroPersonas(reserva.getNumeroPersonas());
			
			Usuario usuario;			
			for (Usuario usurioReserva: reserva.getComensales()){
				usuario = new Usuario();
				usuario = mgr.getObjectById(usurioReserva.getClass(), usurioReserva.getId());				
				log.warning("Comensal: " + usuario.getNombre());
				comensales.add(usuario);				
			}
			nuevaReserva.setComensales(comensales);
			
			Restaurante restaurante = mgr.getObjectById(Restaurante.class, reserva.getRestaurante().getId());
			nuevaReserva.setRestaurante(restaurante);
											
			Query query = mgr.newQuery(Usuario.class, "cuentaGoogle == u");											
			User user = new User(usuarioOAuth.getEmail(), usuarioOAuth.getAuthDomain(), null);														
			query.declareParameters("com.google.appengine.api.users.User u");			
			List<Usuario >execute = (List<Usuario>) query.execute(user);
			
			nuevaReserva.setUsuario(execute.get(0));
			
			reserva = mgr.makePersistent(nuevaReserva);
			log.warning("ID RESERVA: " + reserva.getId());
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw new Exception(e.getMessage());								
		} finally {
			mgr.close();
		}
		return reserva;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param reserva the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateReserva")
	public Reserva updateReserva(Reserva reserva) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsReserva(reserva)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(reserva);
		} finally {
			mgr.close();
		}
		return reserva;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeReserva")
	public void removeReserva(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Reserva reserva = mgr.getObjectById(Reserva.class, id);
			mgr.deletePersistent(reserva);
		} finally {
			mgr.close();
		}
	}

	private boolean containsReserva(Reserva reserva) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Reserva.class, reserva.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
