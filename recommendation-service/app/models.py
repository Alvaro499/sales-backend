from pydantic import BaseModel
from typing import List

class Recommendation(BaseModel):
    product_id: int
    name: str
    score: float

class RecommendationResponse(BaseModel):
    recommendations: List[Recommendation]
