import java.util.*;

public class StuckWin {

	static final Scanner input = new Scanner(System.in);
	
	public static final String RED_BACKGROUND = "\033[41m"; // RED
	public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
	public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE
	public static final String RESET = "\033[0m"; // Text Reset
	public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK

	private static final double BOARD_SIZE = 7;

	public static final char[] LISTLETTER = {'A','B','C','D','E','F','G'};
	public static final char[] LISTNUMBER= {'0','1','2','3','4','5','6','7'};

	enum Result {
		OK
		, BAD_COLOR
		, DEST_NOT_FREE
		, EMPTY_SRC
		, TOO_FAR
		, EXT_BOARD
		, EXIT
	}

	enum ModeMvt {
		REAL
		, SIMU
	}

	final char[] joueurs = { 'B', 'R' };
	final int SIZE = 8;
	final char VIDE = '.';
	// 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas
	char[][] state = {
			{ '-', '-', '-', '-', 'R', 'R', 'R', 'R' },
			{ '-', '-', '-', '.', 'R', 'R', 'R', 'R' },
			{ '-', '-', '.', '.', '.', 'R', 'R', 'R' },
			{ '-', 'B', 'B', '.', '.', '.', 'R', 'R' },
			{ '-', 'B', 'B', 'B', '.', '.', '.', '-' },
			{ '-', 'B', 'B', 'B', 'B', '.', '-', '-' },
			{ '-', 'B', 'B', 'B', 'B', '-', '-', '-' },
	};	

	/**
	 * Déplace un pion ou simule son déplacement
	 * 
	 * @param couleur  couleur du pion à déplacer
	 * @param lcSource case source Lc
	 * @param lcDest   case destination Lc
	 * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le
	 *                 déplacement ou qu'on le simule seulement.
	 * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
	 *         EXIT} selon le déplacement
	 */
	Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {

		Result result = Result.OK;

		// Tableau qui stock les case possiblement jouable
		String[] possibleDests = new String[3];

		// Stock la valeur de la case de départ & d'arrivé : currentCase = R ; B ; - ; . 
		char currentCase;
		char destCase;

		int xSource=0;
		int ySource=0;
		int xDest=0;
		int yDest=0;

		// Vérification que le joueur entre 2 caractère et qu'il corresponde au élement des tableau
		if(lcSource.length() != 2 || lcDest.length() != 2)
		{
			result = Result.EMPTY_SRC;return result;
		}

		if(!(issetlc(lcSource)) || !(issetlc(lcDest)))
		{
			result = Result.EMPTY_SRC;return result;
		}

		// Initialisation de x-y Source et Dest  

		for(int i =0; i < LISTLETTER.length; i++)
		{
			if(LISTLETTER[i] == lcSource.charAt(0))
			{
				xSource = i;
			}

			if(LISTLETTER[i] == lcDest.charAt(0))
			{
				xDest = i;
			}
		}

		for(int i =0; i < LISTNUMBER.length; i++)
		{
			if(LISTNUMBER[i] == lcSource.charAt(1))
			{
				ySource = i;
			}

			if(LISTNUMBER[i] == lcDest.charAt(1))
			{
				yDest = i;
			}
		}

		// Initialisation de currentCase & destCase

		currentCase = state[xSource][ySource];
		destCase = state[xDest][yDest];

		// Vérifie qu'il existe un pion dans la case
		if(emptylc(currentCase)){
			result = Result.EMPTY_SRC; return result;
		}
		
		// Vérification couleur du pion à déplace = couleur du joueur
		if(currentCase != couleur){
			result = Result.BAD_COLOR; return result;
		}

		// Vérifie que la case d'arriver est dans les bordure
		if(destCase == '-')
		{
			result = Result.EXT_BOARD;return result;
		}

		// Vérifie que la case d'arriver n'est pas occuper
		if(destCase != VIDE)
		{
			result = Result.DEST_NOT_FREE; return result;
		}

		// Verifie la distance entre la case de départ et la case d'arrivé
		possibleDests = possibleDests(couleur, xSource, ySource);

		if(!valideDistanceSrcToDest(possibleDests, lcDest))
		{
			result = Result.TOO_FAR;return result;
		}

		// Déplacement du pion
		System.out.print("Success");
		state[xDest][yDest] = state[xSource][ySource];
		state[xSource][ySource] = VIDE;
		
		result = Result.OK; return result;
	}
	


		/**
	 * Verifie si la case que on souhaite jouer existe dans le tableau
	 * @param lcSource La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean issetlc(String lcSource){
		boolean issetLC = false;
		boolean issetL = false;
		boolean issetC = false;

		for(int i=0; i<LISTNUMBER.length;i++){
			if(LISTNUMBER[i] == lcSource.charAt(1)){
				issetC = true;
			}
		}

		for(int i=0; i <LISTLETTER.length; i++){
			if(LISTLETTER[i] == lcSource.charAt(0)){
				issetL = true;
			}
		}

		if(issetC && issetL){
			issetLC= true;
		}

		return issetLC;
	}

	/**
	 * Verifie si il existe un pion dans la case que on souhaite jouer
	 * à partir de la position de départ currentCase.
	 * @param currentCase La case du tableau que on jeu jouer
	 * @return true si il existe un pion sinon false.
	 */
	public boolean emptylc(char currentCase){
		boolean emptylc = true;
		for(int i=0; i < joueurs.length; i++){
			if(currentCase == joueurs[i]){
				emptylc = false;
			}
		}
		return emptylc;
	}

		/**
	 * Verifie si la distance entre currentCase et DestCase et valide
	 * à partir de la position de départ lcDest.
	 * @param possibleDests tableau des trois positions jouables par le pion
	 * @param lcDest id de la case de déplacement souhaité
	 * @return true si la distance et valide sinon false.
	 */
	public boolean valideDistanceSrcToDest(String[] possibleDests, String lcDest){
		boolean valideDistanceSrcToDest = false;
		
		for(int i = 0; i < possibleDests.length ; i++){ 
			if(!(possibleDests[i].equals(lcDest))){
				valideDistanceSrcToDest = true;
			}
		}
		return valideDistanceSrcToDest;
	}

	/**
	 * Construit les trois chaînes représentant les positions accessibles
	 * à partir de la position de départ [idLettre][idCol].
	 * 
	 * @param couleur  couleur du pion à jouer
	 * @param idLettre id de la ligne du pion à jouer
	 * @param idCol    id de la colonne du pion à jouer
	 * @return tableau des trois positions jouables par le pion (redondance possible
	 *         sur les bords)
	 */
	String[] possibleDests(char couleur, int idLettre, int idCol) {
		String[] possibleDests = new String[3];

		for(int i =0; i < LISTLETTER.length; i++){
			for(int j=0; j< LISTNUMBER.length; j++){
				if(couleur == 'R'){
					if(i == idLettre && j == idCol-1){
						possibleDests[0] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}
					else if(i == idLettre+1 && j == idCol){
						possibleDests[1] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}				
					else if(i == idLettre+1 && j == idCol+1){
						possibleDests[2] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}
				}
				else{
					if(i == idLettre-1 && j == idCol){
						possibleDests[0] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}
					else if(i == idLettre-1 && j == idCol+1){
						possibleDests[1] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}				
					else if(i == idLettre && j == idCol+1){
						possibleDests[2] = ""+LISTLETTER[i]+LISTNUMBER[j];
					}
				}			
			}
		}
		return possibleDests;
	}


	void affiche() {

		// graphiqueAffiche();
		// Déclaration des variable pour parcourire le tableau
		int column, line, diag, space;
        char letterCase,numberCase;

		// Parcours des colonne de la moitier droite du tableau
		for(column = 0; column < state.length; column++){

			// Ajout d'espace pour la partie haute du losange
			for (space = state.length -1; space >= column; space--) {
				System.out.print("  ");
			}
			
			// Parcours des diagonale du haut à droite vers le centre
			for (line = 0, diag = state.length - column; line < 1 + column; line++, diag++){
				
				// Nomination & Numeration des cases 
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				/**  Numeration des colonnes de 0->7 pour 0->7 (nombre colonne dans le tableau) 
				* + Affichage des case (Fond-couleur + Nomination ligne + numeration colon + Reset style)
				* VERIFIER AFFICHAGE AVEC joueurs[0] pour B
				*/
				switch(state[line][diag]){
					case VIDE: System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + letterCase + numberCase+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + letterCase + numberCase + RESET);break;
					case '-': System.out.print("  ");
				}
				// Ajout des espaces entre les cases
				System.out.print("  ");
			}
			// Retour ligne affichange du plateau
			System.out.println();	
		}		

		// Parcours des colonne de la moitier gauche du tableau
		for(column = 0; column < state.length -1; column++){
			
			// Ajout d'espace pour la partie basse du losange
			for (space = 0; space-1 <= column; space++){
				System.out.print("  ");
			}
			// Parcours des diagonale du milieu vers le bas à gauche du tableau
			for (line = 1 + column , diag = 1; diag < state.length - column; line++, diag++){
				
				letterCase = LISTLETTER[line];
				numberCase = LISTNUMBER[diag];

				switch(state[line][diag]){
					case VIDE: System.out.print(WHITE_BACKGROUND + letterCase + numberCase + RESET);break;
					case 'R': System.out.print(RED_BACKGROUND + letterCase + numberCase+ RESET);;break;
					case 'B': System.out.print(BLUE_BACKGROUND + letterCase + numberCase + RESET);break;
					case '-': System.out.print("  ");
				}
				System.out.print("  ");
			}
			System.out.println();
		}
	}

	/**
	 * Joue un tour
	 * 
	 * @param couleur couleur du pion à jouer
	 * @return tableau contenant la position de départ et la destination du pion à
	 *         jouer.
	 */
	String[] jouerIA(char couleur) {
		// votre code ici. Supprimer la ligne ci-dessous.s		
		String src = "";
		String dst = "";
		String[] mvtIa;

		for(int i=0; i < state.length;i++){
			for(int j=0; j < state[i].length;j++){
				if(state[i][j]=='R'){
					src = ""+LISTLETTER[i]+LISTNUMBER[j];
					dst = ""+LISTLETTER[i+1]+LISTNUMBER[j-1];
				}
			}
		}

		return new String[] { src, dst };
	}

	/**
	 * gère le jeu en fonction du joueur/couleur
	 * 
	 * @param couleur
	 * @return tableau de deux chaînes {source,destination} du pion à jouer
	 */
	String[] jouer(char couleur) {
		String src = "";
		String dst = "";
		String[] mvtIa;
		switch (couleur) {
			case 'B':
				System.out.println("Mouvement " + couleur);
				src = input.next();
				dst = input.next();
				System.out.println(src + "->" + dst);
				break;
			case 'R':
				System.out.println("Mouvement " + couleur);
				mvtIa = jouerIA(couleur);
				src = mvtIa[0];
				dst = mvtIa[1];
				System.out.println(src + "->" + dst);
				break;
		}
		return new String[] { src, dst };
	}

	/**
	 * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
	 * 
	 * @param couleur
	 * @return
	 */
	char finPartie(char couleur) {
		// votre code ici. Supprimer la ligne ci-dessous.
		return 'N';
	}

	public static void main(String[] args) {
		StuckWin jeu = new StuckWin();
		String src = "";
		String dest;
		String[] reponse;
		Result status;
		char partie = 'N';
		char curCouleur = jeu.joueurs[0];
		char nextCouleur = jeu.joueurs[1];
		char tmp;
		int cpt = 0;

		// version console
		do {
			// séquence pour Bleu ou rouge
			jeu.affiche();
			do {
				status = Result.EXIT;
				reponse = jeu.jouer(curCouleur);
				src = reponse[0];
				dest = reponse[1];
				if ("q".equals(src))
					return;
				status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
				partie = jeu.finPartie(nextCouleur);
				System.out.println("status : " + status + " partie : " + partie);
			} while (status != Result.OK && partie == 'N');
			tmp = curCouleur;
			curCouleur = nextCouleur;
			nextCouleur = tmp;
			cpt++;
		} while (partie == 'N'); // TODO affiche vainqueur
		System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
	}
}
