package com.nerdygadgets.design;

public class Looping {
    private static int maxLoop = 8;
    private static int[] wbAantal = {0,0,0};
    //    private static double[] wbBeschikbaarheid = {0.95,0.9,0.8};
//    private static double[] wbKosten = {5100,3200,2200};
//    private static double[] wbBeschikbaarheid = {0.8,0.95,0.9};
//    private static double[] wbKosten = {2200,5100,3200};
    private static double[] wbBeschikbaarheid = {0.9,0.8,0.95};
    private static double[] wbKosten = {3200,2200,5100};
    private static int maxWBSrt = 2;
    private static int[] dbAantal = {0,0,0};
    //    private static double[] dbBeschikbaarheid = {0.98,0.95,0.9};
//    private static double[] dbKosten = {12200,7700,5100};
//    private static double[] dbBeschikbaarheid = {0.98,0.9,0.95};
//    private static double[] dbKosten = {12200,5100,7700};
    private static double[] dbBeschikbaarheid = {0.9,0.95,0.98};
    private static double[] dbKosten = {5100,7700,12200};
    private static int maxDBSrt = 2;
    private static double minBeschikbaarheid = 0.9999;
    private static double minKosten = Double.MAX_VALUE;
    private static int totaalTeller = 0;
    private static String serverset;

    public static void main(String[] args) {
        LoopWB(0,0);
        System.out.println(totaalTeller + " combinaties onderzocht " + minKosten + " - " + serverset);
    }

    private static int LoopWB(int totaalSrvrWB,int srvnr){
        int teller = 0;
        while(teller<maxLoop-totaalSrvrWB){
            wbAantal[srvnr]= teller;
            if (srvnr<maxWBSrt) {
                LoopWB(teller+totaalSrvrWB,srvnr+1);
            }
            if(srvnr==maxWBSrt){
                LoopDB(0,0);

            }

            teller++;
        }
        return srvnr;
    }

    private static int LoopDB(int totaalSrvrDB, int srvnr){
        int teller = 0;
        while(teller<maxLoop-totaalSrvrDB){
            dbAantal[srvnr]= teller;
            teller++;
            if (srvnr<maxDBSrt) {

                LoopDB(teller+totaalSrvrDB,srvnr+1);
            }

            if(srvnr==maxDBSrt) {
                totaalTeller++;
                System.out.println(totaalTeller + " W " + wbAantal[0] + "-" + wbAantal[1] + "-" + wbAantal[2] + "-" + "D " + dbAantal[0] + "-" + dbAantal[1] + "-" + dbAantal[2] + " ->" + BerekenBeschikbaarheid() + " - " + Berekenkosten() + " " + minKosten + serverset);

                double berekendeBeschikbaarheid = BerekenBeschikbaarheid();
                double berekendeKosten = Berekenkosten();

                if (berekendeBeschikbaarheid > minBeschikbaarheid) {
                    if (berekendeKosten < minKosten) {
                        minKosten = berekendeKosten;
                        serverset = "F 1 W " + wbAantal[0] + "-" + wbAantal[1] + "-" + wbAantal[2] + "-" + "D " + dbAantal[0] + "-" + dbAantal[1] + "-" + dbAantal[2];
                    }
                    return srvnr;
                }
            }
        }
        return srvnr;
    }

    private static double BerekenBeschikbaarheid(){
        double beschikbaarheidFW = 1 - Math.pow((1 - 0.99998), 1);
        double beschikbaarheidDB = 1 - Math.pow((1 - dbBeschikbaarheid[0]), dbAantal[0]) * Math.pow((1 - dbBeschikbaarheid[1]), dbAantal[1]) * Math.pow((1 - dbBeschikbaarheid[2]), dbAantal[2]);
        double beschikbaarheidWB = 1 - Math.pow((1 - wbBeschikbaarheid[0]), wbAantal[0]) * Math.pow((1 - wbBeschikbaarheid[1]), wbAantal[1]) * Math.pow((1 - wbBeschikbaarheid[2]), wbAantal[2]);
        double totaleBeschikbaarheid = beschikbaarheidFW * beschikbaarheidDB * beschikbaarheidWB;

        return totaleBeschikbaarheid;
    }

    private static double Berekenkosten(){
        double kostenFW = 4000;
        double kostenDB = (dbAantal[0] * dbKosten[0]) + (dbAantal[1] * dbKosten[1]) + (dbAantal[2] *dbKosten[2]);
        double kostenWB = (wbAantal[0] * wbKosten[0]) + (wbAantal[1] * wbKosten[1]) + (wbAantal[2] * wbKosten[2]);
        double totalekosten = kostenFW + kostenDB + kostenWB;

        return totalekosten;
    }
}

