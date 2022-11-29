Poker 5 kartowy, dobierany
Autor: Filip Gieracki

Krótkie omówienie zaimplementowanych zasad:
- Gra 5 kartowa, dobierany
- Gra z 2 do 4 graczy (wybór maksymalnej liczby graczy podczas uruchamiania serwera)

Przebieg gry gry:
Na początku gry wybierany jest dealer.
Następnie kolejny gracz gra smallBlind'a, następny bigBlind'a.
Jest to początek negocjacji zakładów.
Gracz po lewej stronie bigBlind'a może zrobić raise, czyli podwoić zakład.
Wszyscy gracze po lewej stronie gracza, który zrobił raise, mogą zrobić kolejny raise, czyli podwoić zakład.

Po zakończeniu pierwszej tury negocjacji rozpoczyna się dobieranie kart
Każdy z graczy może dobrać od 0 do 4 kart (karty na rękach graczy są zamieniane na nowe)

Po zakończonej fazie dobierania kart rozpoczyna się druga tura negocjacji zakładów.
Przebiega ona analogicznie do pierwszej tury, z wyłączeniem smallBlinda i bigBlinda.

Sposób wybierania zwycięzcy:
z graczy, którzy pozostali w grze podczas negocjacji zakładów, wybierany jest gracz, który ma najlepszą rękę.
W przypadku, gdy 2 lub więcej graczy ma taki sam handValue, wybierani są gracze, którzy mają najlepszą kombinację kart na ręce.
W przypadku ponownego remisu, wygrywa gracz z najwyższym kolorem.

Hierarchia kolorów: (od najmocniejszego do najsłabszego)
1. Clubs
2. Diamonds
3. Hearts
4. Spades

Hierarchia kart - standardowa:
2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A


Uruchomienie programu:
w katalogu run/ znajdują się pliki .jar serwera oraz klienta.
Należy uruchomić serwer za pomocą komendy: java -jar poker-server.jar <liczba graczy>,
a następnie kolejno uruchomić klientów: java -jar poker-client.jar


Komunikacja (komendy użytkowników):
!ready <startowa liczba Chipsów> - zgłoszenie gotowości do gry oraz ustalenie początkowej liczby chipsów
    Odpowiedź: serwer potwierdza zgłoszenie gotowości
Gdy wszyscy gracze są gotowi, rozgrywka zaczyna się automatycznie.

Faza negocjacji:
!bet call - wyrównanie do aktualnego zakładu
!bet raise <liczba chipsów> - podbicie zakładu (wymagane podbicie minimum 2x aktualnego najwyższego zakładu)
!bet fold - opuszczenie gry
!bet allin - podbicie zakładu do maksymalnej posiadanej liczby chipsów

W przypadku sukcesu, serwer zwróci komunikat potwierdzający ruch,a inni użytkownicy zobaczą komunikat o wykonanym ruchu.
W przypadku niepowodzenia, serwer zwróci komunikat o błędzie, wówczas do skutku należy powtarzać komendę.

Faza dobierania kart:
!draw <numery kart> - dobieranie karty o podanym numerze (numeracja kart od 1 do 5)
użycie numeru spoza zakresu jest traktowane jako rezygnacja z dalszego dobierania kart
przykładowe użycie: !draw 1 3 5 - wymiana kart o numerach 1, 3 i 5

W przypadku sukcesu, serwer zwróci komunikat potwierdzający ruch,a inni użytkownicy zobaczą komunikat o liczbie wymienionych kart.
W przypadku niepowodzenia, serwer zwróci komunikat o błędzie, wówczas do skutku należy powtarzać komendę.

Faza drugiej negocjacji:
Komendy takie same jak w pierwszej fazie,
po zakończenu tej fazy rozpoczyna się wybór zwycięzcy.
Po wybraniu zwycięzcy, serwer zwróci komunikat o wygranej, a następnie rozpocznie się kolejna runda gry.
