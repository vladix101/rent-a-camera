package com.projekat.backend.service;

import com.projekat.backend.dto.FotoaparatDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Specifikcija;
import com.projekat.backend.repository.FotoaparatRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FotoaparatService {

    private final FotoaparatRepository fotoaparatRepository;
    private final IznajmljivanjeRepository iznajmljivanjeRepository;

    @Transactional(readOnly = true)
    public List<FotoaparatDto> getFotoaparati(LocalDate datumOd, LocalDate datumDo) {
        LocalDate periodOd = datumOd != null ? datumOd : LocalDate.now();
        LocalDate periodDo = datumDo != null ? datumDo : periodOd.plusDays(1);

        Set<Long> zauzetiFotoaparatIds = iznajmljivanjeRepository
                .findByDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(periodDo, periodOd)
                .stream()
                .map(Iznajmljivanje::getFotoaparat)
                .map(Fotoaparat::getId)
                .collect(Collectors.toSet());

        return fotoaparatRepository.findAll()
                .stream()
                .map(fotoaparat -> toDto(fotoaparat, zauzetiFotoaparatIds))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ZauzetostDto> getZauzetPeriodi(Long fotoaparatId) {
        if (!fotoaparatRepository.existsById(fotoaparatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fotoaparat nije pronađen");
        }

        return iznajmljivanjeRepository.findByFotoaparatId(fotoaparatId)
                .stream()
                .map(iznajmljivanje -> new ZauzetostDto(iznajmljivanje.getDatumPocetka(), iznajmljivanje.getDatumKraja()))
                .toList();
    }

    private FotoaparatDto toDto(Fotoaparat fotoaparat, Set<Long> zauzetiFotoaparatIds) {
        Specifikcija specifikcija = fotoaparat.getSpecifikcije().isEmpty() ? null : fotoaparat.getSpecifikcije().get(0);
        boolean dostupanZaPeriod = Boolean.TRUE.equals(fotoaparat.getDostupan()) && !zauzetiFotoaparatIds.contains(fotoaparat.getId());

        return new FotoaparatDto(
                fotoaparat.getId(),
                fotoaparat.getDatumKupovine(),
                fotoaparat.getNapomena(),
                fotoaparat.getDostupan(),
                fotoaparat.getProizvodjac() == null ? null : fotoaparat.getProizvodjac().getName(),
                specifikcija == null || specifikcija.getKategorija() == null ? null : specifikcija.getKategorija().getNaziv(),
                specifikcija == null ? null : specifikcija.getRezolucija(),
                specifikcija == null ? null : specifikcija.getSenzorSlike(),
                specifikcija == null ? null : specifikcija.getWifi(),
                specifikcija == null ? null : specifikcija.getEkran(),
                specifikcija == null ? null : specifikcija.getNapajanje(),
                specifikcija == null ? null : specifikcija.getVelicinaSlike(),
                specifikcija == null ? null : specifikcija.getOpis(),
                dostupanZaPeriod
        );
    }
}
