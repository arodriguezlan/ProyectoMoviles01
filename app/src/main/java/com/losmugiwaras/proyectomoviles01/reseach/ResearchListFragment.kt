import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.proyectomoviles01.R
import com.losmugiwaras.proyectomoviles01.reseach.ResearchAdapter

class ResearchListFragment : Fragment() {

    // Listas para almacenar la investigación original y los resultados filtrados
    private lateinit var researchList: MutableList<Research> // Lista completa de investigaciones
    private lateinit var filteredList: MutableList<Research> // Lista de investigaciones filtradas

    // Adaptador y vistas de la interfaz
    private lateinit var adapter: ResearchAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchTopic: EditText // Campo de búsqueda por tema
    private lateinit var searchGrade: EditText // Campo de búsqueda por grado académico

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_research_list, container, false)

        // Inicializa las listas y vistas
        researchList = mutableListOf()
        filteredList = mutableListOf()
        recyclerView = view.findViewById(R.id.recyclerView)
        searchTopic = view.findViewById(R.id.searchTopic)
        searchGrade = view.findViewById(R.id.searchGrade)

        // Configura el RecyclerView con un adaptador y un LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ResearchAdapter(filteredList) { research ->
            // Configura la acción al seleccionar un elemento
            navigateToPresentationFragment(research.id)
        }
        recyclerView.adapter = adapter

        // Carga los datos desde Firebase Firestore
        fetchResearchData()

        // Configura los filtros para actualizar automáticamente
        setupFilters()

        return view
    }

    // Método para obtener datos de investigación desde Firestore
    private fun fetchResearchData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("research")
            .get()
            .addOnSuccessListener { result ->
                // Limpia la lista antes de agregar nuevos elementos
                researchList.clear()
                for (document in result) {
                    // Crea objetos Research con los datos obtenidos
                    val research = Research(
                        document.id,
                        document.getString("title") ?: "", // Título de la investigación
                        document.getString("topic") ?: "", // Tema de la investigación
                        document.getString("academicGrade") ?: "" // Grado académico
                    )
                    researchList.add(research)
                }
                // Aplica los filtros a la lista actualizada
                applyFilters()
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                exception.printStackTrace()
            }
    }

    // Configura los filtros para actualizar en tiempo real mientras el usuario escribe
    private fun setupFilters() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aplica los filtros cada vez que cambia el texto
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        // Agrega el TextWatcher a los campos de texto
        searchTopic.addTextChangedListener(textWatcher)
        searchGrade.addTextChangedListener(textWatcher)
    }

    // Filtra los datos según el tema y el grado académico ingresados
    private fun applyFilters() {
        val topic = searchTopic.text.toString().lowercase() // Convierte a minúsculas para comparación
        val grade = searchGrade.text.toString().lowercase()

        filteredList.clear()
        // Filtra la lista original basada en las condiciones ingresadas
        filteredList.addAll(
            researchList.filter {
                it.topic.lowercase().contains(topic) &&
                        it.academicGrade.lowercase().contains(grade)
            }
        )
        // Notifica al adaptador que los datos han cambiado
        adapter.notifyDataSetChanged()
    }

    // Navega al fragmento de presentación para mostrar detalles
    private fun navigateToPresentationFragment(researchId: String) {
        val fragment = PresentationFragment.newInstance(researchId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Reemplaza el fragmento actual
            .addToBackStack(null) // Agrega la transacción al back stack para navegación inversa
            .commit()
    }
}
