from sqlalchemy.orm import Session
from app.database import SessionLocal
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def get_content_based_recommendations(product_id: int):
    try:
        db: Session = SessionLocal()
        query = "SELECT product_id, name, description FROM products"
        df = pd.read_sql(query, db.bind)
        db.close()

        if df.empty or product_id not in df['product_id'].values:
            return {"recommendations": []}

        # Asegurar que no haya descripciones nulas
        df['description'] = df['description'].fillna('')

        # Vectorización TF-IDF
        tfidf = TfidfVectorizer(stop_words='english')
        tfidf_matrix = tfidf.fit_transform(df['description'])

        # Similitud coseno
        cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)
        indices = pd.Series(df.index, index=df['product_id'])

        idx = indices[product_id]
        sim_scores = list(enumerate(cosine_sim[idx]))
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)[1:4]
        product_indices = [i[0] for i in sim_scores]

        recommendations = []
        for i in product_indices:
            row = df.iloc[i]
            recommendations.append({
                "product_id": int(row['product_id']),
                "name": row.get("name", f"Producto {row['product_id']}"),
                "score": round(sim_scores[i][1], 2)
            })

        return {"recommendations": recommendations}

    except Exception as e:
        return {"error": str(e)}