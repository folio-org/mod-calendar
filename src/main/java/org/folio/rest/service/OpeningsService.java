package org.folio.rest.service;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;

import org.folio.rest.beans.Openings;

public interface OpeningsService {

  Future<Void> saveOpenings(AsyncResult<SQLConnection> conn, Openings openings);

  Future<List<Openings>> findOpeningsById(AsyncResult<SQLConnection> conn, String openingId);

  Future<List<Openings>> findOpeningsByServicePointId(AsyncResult<SQLConnection> conn, String servicePointId);

  Future<Void> updateOpenings(AsyncResult<SQLConnection> conn, Openings openings);

  Future<Void> deleteOpeningsById(AsyncResult<SQLConnection> conn, String openingsId);

  Future<Void> checkOpeningsForOverlap(AsyncResult<SQLConnection> conn, Openings openings);
}
