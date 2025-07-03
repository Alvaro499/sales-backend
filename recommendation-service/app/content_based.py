from sqlalchemy.orm import Session
from app.database import SessionLocal
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from uuid import UUID
import logging

def get_content_based_recommendations(product_id: UUID):
    try:
        db: Session = SessionLocal()
        query = "SELECT product_id, name, description FROM products"
        df = pd.read_sql(query, db.bind)
        db.close()

        if df.empty or str(product_id) not in df['product_id'].astype(str).values:
            return {"recommendations": []}

        # Preprocesamiento
        df['description'] = df['description'].fillna('')
        tfidf = TfidfVectorizer(stop_words='english')
        tfidf_matrix = tfidf.fit_transform(df['description'])

        # Índices
        df['product_id_str'] = df['product_id'].astype(str)
        indices = pd.Series(df.index, index=df['product_id_str'])

        idx = indices[str(product_id)]
        sim_scores = list(enumerate(cosine_similarity(tfidf_matrix[idx], tfidf_matrix)[0]))
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)[1:4]
        product_indices = [i[0] for i in sim_scores]

        recommendations = []
        for i in product_indices:
            row = df.iloc[i]
            recommendations.append({
                "product_id": row['product_id'],
                "name": row.get("name", f"Producto {row['product_id']}"),
                "score": round(sim_scores[product_indices.index(i)][1], 2)
            })

        return {"recommendations": recommendations}

    except Exception as e:
        logging.error(f"Error en content-based: {e}")
        return {"recommendations": []}