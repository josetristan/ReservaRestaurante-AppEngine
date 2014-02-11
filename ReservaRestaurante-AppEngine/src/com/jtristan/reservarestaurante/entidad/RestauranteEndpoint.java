package com.jtristan.reservarestaurante.entidad;

import com.jtristan.reservarestaurante.entidad.PMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "restauranteendpoint", namespace = @ApiNamespace(ownerDomain = "jtristan.com", ownerName = "jtristan.com", packagePath = "reservarestaurante.entidad"))
public class RestauranteEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listRestaurante")
	public CollectionResponse<Restaurante> listRestaurante(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Restaurante> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Restaurante.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Restaurante>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Restaurante obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Restaurante> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	
	@ApiMethod(name = "getCarta")
	public List<Carta> getCarta(@Named("id") Long id) {
	    PersistenceManager mgr = getPersistenceManager();
	    Restaurante restaurante = null;
	    List<Carta> platos = null;
	    try {                   
	        restaurante = mgr.getObjectById(Restaurante.class, id);
	        platos = restaurante.getCarta();
	    } finally {
	        mgr.close();
	    }
	    return platos;
	}


	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getRestaurante")
	public Restaurante getRestaurante(@Named("id") Long id){
		PersistenceManager mgr = getPersistenceManager();
		Restaurante restaurante = null;
		try {
			restaurante = mgr.getObjectById(Restaurante.class, id);
		} finally {
			mgr.close();
		}
		return restaurante;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param restaurante the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertRestaurante")
	public Restaurante insertRestaurante(Restaurante restaurante) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsRestaurante(restaurante)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(restaurante);
		} finally {
			mgr.close();
		}
		return restaurante;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param restaurante the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateRestaurante")
	public Restaurante updateRestaurante(Restaurante restaurante) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsRestaurante(restaurante)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(restaurante);
		} finally {
			mgr.close();
		}
		return restaurante;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeRestaurante")
	public void removeRestaurante(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Restaurante restaurante = mgr.getObjectById(Restaurante.class, id);
			mgr.deletePersistent(restaurante);
		} finally {
			mgr.close();
		}
	}

	private boolean containsRestaurante(Restaurante restaurante) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Restaurante.class, restaurante.getId());
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
