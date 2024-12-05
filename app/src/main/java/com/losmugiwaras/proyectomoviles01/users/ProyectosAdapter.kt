import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.proyectomoviles01.R

class ProyectosAdapter(private var proyectos: List<Proyecto>) :
    RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder>() {

    class ProyectoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tituloTextView: TextView = view.findViewById(R.id.titulo)
        val areaTextView: TextView = view.findViewById(R.id.area)
        val autorTextView: TextView = view.findViewById(R.id.autor)
        val descripcionTextView: TextView = view.findViewById(R.id.descripcion)
        val pdfButton: Button = view.findViewById(R.id.descargarPDF)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        val proyecto = proyectos[position]
        holder.tituloTextView.text = proyecto.titulo
        holder.areaTextView.text = proyecto.area
        holder.autorTextView.text = proyecto.autor
        holder.descripcionTextView.text = proyecto.descripcion
        holder.pdfButton.setOnClickListener {
            // Lógica para descargar PDF
        }
    }

    override fun getItemCount(): Int = proyectos.size

    fun updateProyectos(proyectos: List<Proyecto>) {
        this.proyectos = proyectos
        notifyDataSetChanged() // Actualizar la lista
    }
}
