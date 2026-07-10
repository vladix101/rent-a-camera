package com.projekat.backend.service;

import com.projekat.backend.dto.FotoaparatDto;
import com.projekat.backend.dto.FotoaparatRequestDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Kategorija;
import com.projekat.backend.entity.Proizvodjac;
import com.projekat.backend.entity.Specifikacija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.FotoaparatRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KategorijaRepository;
import com.projekat.backend.repository.ProizvodjacRepository;
import com.projekat.backend.repository.SpecifikacijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FotoaparatService {

    private final FotoaparatRepository fotoaparatRepository;
    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final KategorijaRepository kategorijaRepository;
    private final ProizvodjacRepository proizvodjacRepository;
    private final SpecifikacijaRepository specifikacijaRepository;

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

    @Transactional
    public FotoaparatDto createFotoaparat(FotoaparatRequestDto requestDto) {
        validateFotoaparat(requestDto);

        Kategorija kategorija = kategorijaRepository.findById(requestDto.getKategorijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija nije pronađena"));
        Proizvodjac proizvodjac = proizvodjacRepository.findById(requestDto.getProizvodjacId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proizvođač nije pronađen"));
        Specifikacija specifikacija = specifikacijaRepository.findById(requestDto.getSpecifikacijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specifikacija nije pronađena"));

        Fotoaparat fotoaparat = new Fotoaparat();
        applyFields(fotoaparat, requestDto, kategorija, proizvodjac, specifikacija);

        fotoaparat = fotoaparatRepository.save(fotoaparat);
        return toDto(fotoaparat, Set.of());
    }

    @Transactional
    public FotoaparatDto updateFotoaparat(Long id, FotoaparatRequestDto requestDto) {
        validateFotoaparat(requestDto);

        Fotoaparat fotoaparat = fotoaparatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fotoaparat nije pronađen"));
        Kategorija kategorija = kategorijaRepository.findById(requestDto.getKategorijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija nije pronađena"));
        Proizvodjac proizvodjac = proizvodjacRepository.findById(requestDto.getProizvodjacId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proizvođač nije pronađen"));
        Specifikacija specifikacija = specifikacijaRepository.findById(requestDto.getSpecifikacijaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specifikacija nije pronađena"));

        applyFields(fotoaparat, requestDto, kategorija, proizvodjac, specifikacija);

        fotoaparat = fotoaparatRepository.save(fotoaparat);
        return toDto(fotoaparat, Set.of());
    }

    @Transactional
    public void deleteFotoaparat(Long id) {
        Fotoaparat fotoaparat = fotoaparatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fotoaparat nije pronađen"));

        boolean imaIznajmljivanja = !iznajmljivanjeRepository.findByFotoaparatId(id).isEmpty();
        if (imaIznajmljivanja) {
            fotoaparat.setDostupan(false);
            fotoaparatRepository.save(fotoaparat);
        } else {
            fotoaparatRepository.delete(fotoaparat);
        }
    }

    private void applyFields(Fotoaparat fotoaparat, FotoaparatRequestDto requestDto, Kategorija kategorija,
                              Proizvodjac proizvodjac, Specifikacija specifikacija) {
        fotoaparat.setDatumKupovine(requestDto.getDatumKupovine());
        fotoaparat.setNapomena(requestDto.getNapomena());
        fotoaparat.setDostupan(requestDto.getDostupan() != null ? requestDto.getDostupan() : true);
        fotoaparat.setKategorija(kategorija);
        fotoaparat.setProizvodjac(proizvodjac);
        fotoaparat.setSpecifikacija(specifikacija);
    }

    private void validateFotoaparat(FotoaparatRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getProizvodjacId() == null) {
            fieldErrors.put("proizvodjacId", "Proizvođač je obavezan");
        }
        if (requestDto.getKategorijaId() == null) {
            fieldErrors.put("kategorijaId", "Kategorija je obavezna");
        }
        if (requestDto.getSpecifikacijaId() == null) {
            fieldErrors.put("specifikacijaId", "Specifikacija je obavezna");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private FotoaparatDto toDto(Fotoaparat fotoaparat, Set<Long> zauzetiFotoaparatIds) {
        Specifikacija specifikacija = fotoaparat.getSpecifikacija();
        Kategorija kategorija = fotoaparat.getKategorija();
        Proizvodjac proizvodjac = fotoaparat.getProizvodjac();
        boolean dostupanZaPeriod = Boolean.TRUE.equals(fotoaparat.getDostupan()) && !zauzetiFotoaparatIds.contains(fotoaparat.getId());

        return new FotoaparatDto(
                fotoaparat.getId(),
                fotoaparat.getDatumKupovine(),
                fotoaparat.getNapomena(),
                fotoaparat.getDostupan(),
                proizvodjac == null ? null : proizvodjac.getId(),
                proizvodjac == null ? null : proizvodjac.getName(),
                kategorija == null ? null : kategorija.getId(),
                kategorija == null ? null : kategorija.getNaziv(),
                specifikacija == null ? null : specifikacija.getId(),
                specifikacija == null ? null : specifikacija.getRezolucija(),
                specifikacija == null ? null : specifikacija.getSenzorSlike(),
                specifikacija == null ? null : specifikacija.getWifi(),
                specifikacija == null ? null : specifikacija.getEkran(),
                specifikacija == null ? null : specifikacija.getNapajanje(),
                specifikacija == null ? null : specifikacija.getVelicinaSlike(),
                specifikacija == null ? null : specifikacija.getOpis(),
                dostupanZaPeriod
        );
    }
}
