package com.projekat.backend.service;

import com.projekat.backend.dto.FotoaparatDto;
import com.projekat.backend.dto.FotoaparatRequestDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Kategorija;
import com.projekat.backend.entity.Proizvodjac;
import com.projekat.backend.entity.Specifikcija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.FotoaparatRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KategorijaRepository;
import com.projekat.backend.repository.ProizvodjacRepository;
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
        Proizvodjac proizvodjac = resolveProizvodjac(requestDto.getProizvodjacNaziv());

        Fotoaparat fotoaparat = new Fotoaparat();
        applyFields(fotoaparat, requestDto, proizvodjac);

        Specifikcija specifikcija = new Specifikcija();
        specifikcija.setFotoaparat(fotoaparat);
        applySpecifikcijaFields(specifikcija, requestDto, kategorija);
        fotoaparat.getSpecifikcije().add(specifikcija);

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
        Proizvodjac proizvodjac = resolveProizvodjac(requestDto.getProizvodjacNaziv());

        applyFields(fotoaparat, requestDto, proizvodjac);

        Specifikcija specifikcija = fotoaparat.getSpecifikcije().isEmpty() ? new Specifikcija() : fotoaparat.getSpecifikcije().get(0);
        specifikcija.setFotoaparat(fotoaparat);
        applySpecifikcijaFields(specifikcija, requestDto, kategorija);
        if (fotoaparat.getSpecifikcije().isEmpty()) {
            fotoaparat.getSpecifikcije().add(specifikcija);
        }

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

    private void applyFields(Fotoaparat fotoaparat, FotoaparatRequestDto requestDto, Proizvodjac proizvodjac) {
        fotoaparat.setDatumKupovine(requestDto.getDatumKupovine());
        fotoaparat.setNapomena(requestDto.getNapomena());
        fotoaparat.setDostupan(requestDto.getDostupan() != null ? requestDto.getDostupan() : true);
        fotoaparat.setProizvodjac(proizvodjac);
    }

    private void applySpecifikcijaFields(Specifikcija specifikcija, FotoaparatRequestDto requestDto, Kategorija kategorija) {
        specifikcija.setKategorija(kategorija);
        specifikcija.setRezolucija(requestDto.getRezolucija());
        specifikcija.setSenzorSlike(requestDto.getSenzorSlike());
        specifikcija.setWifi(requestDto.getWifi());
        specifikcija.setEkran(requestDto.getEkran());
        specifikcija.setNapajanje(requestDto.getNapajanje());
        specifikcija.setVelicinaSlike(requestDto.getVelicinaSlike());
        specifikcija.setOpis(requestDto.getOpis());
    }

    private Proizvodjac resolveProizvodjac(String naziv) {
        String trimmedNaziv = naziv == null ? "" : naziv.trim();
        return proizvodjacRepository.findByNameIgnoreCase(trimmedNaziv)
                .orElseGet(() -> {
                    Proizvodjac proizvodjac = new Proizvodjac();
                    proizvodjac.setName(trimmedNaziv);
                    return proizvodjacRepository.save(proizvodjac);
                });
    }

    private void validateFotoaparat(FotoaparatRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getProizvodjacNaziv() == null || requestDto.getProizvodjacNaziv().isBlank()) {
            fieldErrors.put("proizvodjacNaziv", "Proizvođač je obavezan");
        }
        if (requestDto.getKategorijaId() == null) {
            fieldErrors.put("kategorijaId", "Kategorija je obavezna");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
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
                specifikcija == null || specifikcija.getKategorija() == null ? null : specifikcija.getKategorija().getId(),
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
