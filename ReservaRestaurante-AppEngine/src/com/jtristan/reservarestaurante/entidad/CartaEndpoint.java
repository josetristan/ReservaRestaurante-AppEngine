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

@Api(name = "cartaendpoint", namespace = @ApiNamespace(ownerDomain = "jtristan.com", ownerName = "jtristan.com", packagePath = "reservarestaurante.entidad"))
public class CartaEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listCarta")
	public CollectionResponse<Carta> listCarta(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Carta> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Carta.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Carta>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Carta obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Carta> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getCarta")
	public Carta getCarta(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Carta carta = null;
		try {
			carta = mgr.getObjectById(Carta.class, id);
		} finally {
			mgr.close();
		}
		return carta;
	}
		

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param carta the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertCarta")
	public Carta insertCarta(Carta carta) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsCarta(carta)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(carta);
		} finally {
			mgr.close();
		}
		return carta;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param carta the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateCarta")
	public Carta updateCarta(Carta carta) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsCarta(carta)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(carta);
		} finally {
			mgr.close();
		}
		return carta;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeCarta")
	public void removeCarta(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Carta carta = mgr.getObjectById(Carta.class, id);
			mgr.deletePersistent(carta);
		} finally {
			mgr.close();
		}
	}

	private boolean containsCarta(Carta carta) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Carta.class, carta.getClave());
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
