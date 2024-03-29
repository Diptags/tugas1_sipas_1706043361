package com.sipas.app.service;

import com.sipas.app.model.AsuransiModel;
import com.sipas.app.repository.AsuransiDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsuransiServiceImpl implements AsuransiService{

    @Autowired
    AsuransiDb asuransiDb;

    @Override
    public void addAsuransi(AsuransiModel asuransi){
        asuransiDb.save(asuransi);
    }

    @Override
    public List<AsuransiModel> getAsuransiList(){
        return asuransiDb.findAll();
    }

    @Override
    public AsuransiModel getAsuransiByIdAsuransi(Long idAsuransi){
        return asuransiDb.findByIdAsuransi(idAsuransi);
    }

    @Override
    public void deleteAsuransi(AsuransiModel asuransi){
        asuransiDb.delete(asuransi);
    }
}
