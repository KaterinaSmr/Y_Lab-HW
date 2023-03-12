package sequences;

public class SequencesTest {

    public static void main(String[] args) {
        //на мой вягляд, для некооторых последовательнойстей решение со стримами SequencesAlternativeImpl() проще читается,
        //на на всякий случай основым оставлю наверное более ожидаемое решение с циклами SequencesImpl() :)
        Sequences sequences = new SequencesImpl();
        Sequences alternativeSeq = new SequencesAlternativeImpl();

        //считаем что кол-во элементов не более 20
        int amount = 20;
        sequences.a(amount);
        alternativeSeq.a(amount);
        sequences.b(amount);
        alternativeSeq.b(amount);
        sequences.c(amount);
        alternativeSeq.c(amount);
        sequences.d(amount);
        alternativeSeq.d(amount);
        sequences.e(amount);
        alternativeSeq.e(amount);
        sequences.f(amount);
        alternativeSeq.f(amount);
        sequences.g(amount);
        alternativeSeq.g(amount);
        sequences.h(amount);
        alternativeSeq.h(amount);
        sequences.i(amount);
        alternativeSeq.i(amount);
        sequences.j(amount);
        alternativeSeq.j(amount);
    }
}
