package br.com.ilia.digital.folhadeponto.repository;

import br.com.ilia.digital.folhadeponto.models.MomentoVO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MomentoRepository extends CrudRepository<MomentoVO, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM momento WHERE FORMATDATETIME(data_hora, 'yyyy-MM-dd') = :dataMomento ORDER BY data_hora DESC"
    )
    List<MomentoVO> findAllByDate(@Param("dataMomento") LocalDate dataMomento);
}
