package br.com.ilia.digital.folhadeponto.repository;

import br.com.ilia.digital.folhadeponto.models.AlocacaoVO;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlocacaoRepository extends CrudRepository<AlocacaoVO, Integer> {

    List<AlocacaoVO> findAllByDia(LocalDate dia);
}
