package com.petcarebackend.service;

import java.util.List;

/**
 * Generic CRUD servis arayüzü.
 *
 * @param <Res>  Yanıt (response) türü
 * @param <ID>   Birincil anahtar türü
 * @param <CreateReq> Oluşturma isteği türü
 */
public interface CrudService<Res, ID, CreateReq> {

    /** Tüm kayıtları döndürür. */
    List<Res> findAll();

    /** Verilen ID ile kaydı döndürür; bulunamazsa NotFoundException fırlatır. */
    Res findById(ID id);

    /** Yeni kayıt oluşturur ve oluşturulan kaydı döndürür. */
    Res create(CreateReq request);

    /** Varolan kaydı siler; bulunamazsa NotFoundException fırlatır. */
    void delete(ID id);
}
