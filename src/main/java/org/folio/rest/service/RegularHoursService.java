package org.folio.rest.service;

import java.util.List;

import org.folio.rest.beans.RegularHours;
import org.folio.rest.persist.SQLConnection;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

public interface RegularHoursService {

  Future<Void> saveRegularHours(AsyncResult<SQLConnection> conn, RegularHours entity);

  Future<Void> updateRegularHours(AsyncResult<SQLConnection> conn, RegularHours regularHours);

  Future<List<RegularHours>> findRegularHoursByOpeningId(AsyncResult<SQLConnection> conn, String openingId);

  Future<Void> deleteRegularHoursByOpeningsId(AsyncResult<SQLConnection> conn, String openingsId);
}
